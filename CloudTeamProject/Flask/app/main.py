from flask import Flask
from flask import request
import os
import numpy as np

from base64 import b64encode, b64decode, urlsafe_b64decode, urlsafe_b64encode
import zlib, json
import hashlib
import math

app = Flask(__name__)

port = int(os.getenv("VCAP_APP_PORT", 8080))
rsa_constant = 1561906343821 # referred to as n in the write-up
my_public_key = 1097844002039 # referred to as e in the write-up
my_private_key = 343710770439 # referred to as d in the write-up
AWS_acct = "922823096026" # Team ThorAndLoki AWS account number

payment_constant = 500000000

def compute_sha(input):
    return hashlib.sha256(input.encode('utf-8')).hexdigest()

def compute_cchash(input):
    sha = compute_sha(input)
    return sha[:8]

def proof_of_work_hash(input, pow):
    sha = compute_sha(input)
    hash_out = compute_cchash(sha+pow)
    return hash_out

def RSA_encrypt(message, public_key, rsa_constant=rsa_constant):
    # cipher_text is of the form: C = (M ^ e) % n
    cipher_text = pow(message, public_key, rsa_constant)
    return cipher_text

def RSA_decrypt(cipher_text, private_key, rsa_constant=rsa_constant):
    # message is of the form: M = (C ^ d) % n
    message = pow(cipher_text, private_key, rsa_constant)
    return message

def hash_comparison(given, computed):
    if given == computed:
        return 0
    else:
        return -1

def process_transaction(input):
    given_hash = input.get("hash","")
    time = input.get("time","")
    sender = str(input.get("send",""))
    receiver = str(input.get("recv",""))
    amt = str(input.get("amt",""))
    fee = str(input.get("fee",""))
    string = "|".join([time,sender,receiver,amt,fee])
    computed_hash = compute_cchash(string)
    return hash_comparison(given_hash, computed_hash)

def verify_sig(input):
    signature = None
    try:
        signature = input["sig"]
    except:
        pass
    if signature is not None:
        given_hash = input.get("hash",0)
        computed_hash = RSA_encrypt(signature,input.get("send"))
        computed_hash = format(computed_hash, 'x').zfill(8)
        return hash_comparison(given_hash, computed_hash)
    return 0

def extract_data(transaction):
    payment = transaction.get("amt",0)
    fee = transaction.get("fee",0)
    sender = transaction.get("send",0)
    receiver = transaction.get("recv",0)
    return (payment, fee, sender, receiver)

def check_time(new, old):
    if new <= old:
        return (None, -1)
    return (new, 0)

def check_hash(transaction):
    if(process_transaction(transaction) != 0):
        return -1
    return 0

def check_positive(val):
    if val < 0:
        return -1
    return 0

def check_signature(transaction):
    if verify_sig(transaction):
        return -1
    return 0

def check_balances(transaction, user_balances):
    (payment, fee, sender, receiver) = extract_data(transaction)
    if(receiver):
        user_balances[str(receiver)] = user_balances.get(str(receiver),0)+payment
    if(sender):
        remaining = user_balances.get(str(sender),0)-(payment+fee)
        if remaining < 0:
            return (None, -1)
        user_balances[str(sender)] = remaining
    return (user_balances, 0)

def check_block_id(new, old):
    if (new - old) != 1:
        return (None, -1)
    return (new, 0)

def check_proof_of_work(working_pow, block):
    if (proof_of_work_hash(working_pow, block.get("pow","0")) != block.get("hash")):
        return -1
    return 0

def check_payment(prev_block_ID, reward):
    payment = payment_constant
    for i in range(int(prev_block_ID // 2)):
        payment = int(payment // 2)
    if payment != reward:
        return -1
    return 0

def transaction_error_checker(transaction, new_time, prev_time, user_balances):

    if(check_hash(transaction)):
        return -1
    if (check_positive(transaction.get("fee",0))):
        return -1
    if (check_positive(transaction.get("amt",0))):
        return -1
    if (check_signature(transaction)):
        return -1
    prev_time, error_flag = check_time(new_time, prev_time)
    if (error_flag):
        return -1
    user_balances, error_flag = check_balances(transaction, user_balances)
    if (error_flag):
        return -1

    return (prev_time, user_balances)

def validate_request(request):
    chain = request["chain"]
    new_transactions = request["new_tx"]
    full_list_of_transactions = []
    user_balances = {}
    prev_hash = "00000000"
    prev_time = 0
    prev_block_ID = -1

    for block in chain:
        working_pow = str(block.get("id"))
        working_pow += "|" + prev_hash
        minerReward = 0
        transactionList = block["all_tx"]
        for transaction in transactionList:
            if transaction not in full_list_of_transactions:
                full_list_of_transactions.append(transaction)
            else:
                return -1
            try:
                new_time = int(transaction.get("time",0))
                prev_time, user_balances = transaction_error_checker(transaction, new_time, prev_time, user_balances)
            except:
                return -1
            working_pow += "|" + transaction.get("hash")

        if check_proof_of_work(working_pow, block):
            return -1
        prev_block_ID, error_flag = check_block_id(block.get("id",-1), prev_block_ID)
        if error_flag:
            return -1

        prev_hash = block.get("hash")
        reward = int(transaction.get("amt",0))
        if check_payment(prev_block_ID, reward):
            return -1

    for transaction in new_transactions:
        if int(transaction.get("time",prev_time+1)) <= prev_time:
            return -1
        else:
            prev_time = int(transaction["time"])
        if verify_sig(transaction):
            return -1
        user_balances, error_flag = check_balances(transaction, user_balances)
        if error_flag < 0:
            return -1

    return (0, prev_hash, prev_block_ID, new_time)

def update_dict(my_dict, amt, fee, hash_val, recv, send, sig, time, all_tx, id_val, pow_val, target):
    if amt is not None:
        my_dict["amt"] = amt
    if fee is not None:
        my_dict["fee"] = fee
    if hash_val is not None:
        my_dict["hash"] = hash_val
    if recv is not None:
        my_dict["recv"] = recv
    if send is not None:
        my_dict["send"] = send
    if sig is not None:
        my_dict["sig"] = sig
    if time is not None:
        my_dict["time"] = time
    if all_tx is not None:
        my_dict["all_tx"] = all_tx
    if id_val is not None:
        my_dict["id"] = id_val
    if pow_val is not None:
        my_dict["pow"] = pow_val
    if target is not None:
        my_dict["target"] = target
    return my_dict

def mine_for_int(pow_string, new_target):
    pow_val = 0
    result = proof_of_work_hash(pow_string, str(pow_val))

    while(result >= new_target):
        pow_val += 1
        result = proof_of_work_hash(pow_string, str(pow_val))
    return (str(pow_val), result)

def update_transaction_and_hash(tx_list, h_list, value):
    tx_list.append(value)
    h_list.append(value.get("hash"))
    return tx_list, h_list

def create_new_transaction(transaction):
    new_transaction = {}
    string = "|".join([transaction.get("time"),
                       str(my_public_key),
                       str(transaction.get("recv")),
                       str(transaction.get("amt")),
                       "0"])
    hash_val = compute_cchash(string)
    new_transaction = update_dict(new_transaction,
                                  transaction.get("amt"),
                                  0,
                                  hash_val,
                                  transaction.get("recv"),
                                  my_public_key,
                                  RSA_decrypt(int(hash_val,16),my_private_key),
                                  transaction.get("time"),
                                  None,
                                  None,
                                  None,
                                None)
    return new_transaction

def compute_reward(new_time, new_block_ID):
    reward = {}
    reward_time = str(new_time + 600000000000)
    payment = payment_constant
    for i in range(int(new_block_ID // 2)):
        payment = int(payment // 2)
    string = "|".join([reward_time, "", str(my_public_key), str(payment), ""])
    hash_val = compute_cchash(string)
    reward = update_dict(reward,
                         payment,
                         None,
                         hash_val,
                         my_public_key,
                         None,
                         None,
                         reward_time,
                         None,
                         None,
                         None,
                         None)
    return reward

def create_new_block(transaction_list, result, new_block_ID, pow_val, new_target):
    new_block = {}
    new_block["all_tx"] = transaction_list
    new_block["hash"] = result
    new_block["id"] = new_block_ID
    new_block["pow"] = pow_val
    new_block["target"] = new_target
    return new_block

def generate_response(request, prev_hash, prev_block_ID, new_time):

    new_transactions = request.get("new_tx")
    new_target = request.get("new_target")
    transaction_list = []
    hash_list = []
    for transaction in new_transactions:
        if transaction.get("hash", 0) == 0:
            transaction = create_new_transaction(transaction)
        transaction_list, hash_list = update_transaction_and_hash(transaction_list, hash_list, transaction)

    new_block_ID = prev_block_ID + 1
    reward = compute_reward(new_time, new_block_ID)
    transaction_list, hash_list = update_transaction_and_hash(transaction_list, hash_list, reward)

    pow_string = str(new_block_ID) + "|" + str(prev_hash)
    for hash_val in hash_list:
        pow_string+= "|" + str(hash_val)

    pow_val, result = mine_for_int(pow_string, new_target)
    new_block = create_new_block(transaction_list, result, new_block_ID, pow_val, new_target)

    chain = request["chain"]
    chain.append(new_block)
    out = {}
    out["chain"] = chain
    return out

def process_request(request):
    message = "ThorAndLoki" + "," + AWS_acct
    try:
        query = request
        data = json.loads(zlib.decompress(urlsafe_b64decode(query)))
    except:
        message_out = "INVALID | Exception 1"
        return '{}\n{}'.format(message, message_out)

    try:
        error_code, prev_hash, prev_block_ID, new_time = validate_request(data)
    except:
        message_out = "INVALID | Exception 2"
        return '{}\n{}'.format(message, message_out)

    response = generate_response(data, prev_hash, prev_block_ID, new_time)
    message_out = str((urlsafe_b64encode(zlib.compress(json.dumps(response,separators=(',',':')).encode('ascii')))).decode('ascii'))
#     check = "eJyFk9tum0AQht9lr7mYw85heZWqqgBDYilJq9hqK0V-945tNkDspFwhFma--b_hLQ2P3f4ltd_eUvf09OP493L7Og6_U-tFsqg7ebEmdc_H1ArMV5OO--cxtQnFSQgyLFdq0mN3eIzD3JOZK6TT9yb9-vknHp1P97vUwvtLYENxyxYHx-71YTyeq6ZTsyE67B9Si0IcBGzBVZpKyciKBOgUz6YxoCiDz8BI6tlL1hvgcw0TwmIanQ_jy-7jxDPfANm463cXpGtPJcxRg4mt_CcZBCD0qOBScEmm60Un3PkqGQKas8ElGxCZdijrbOB-ODE-EHGGwlazKSBWIDIoWLMxR5KZObtpIbwlLpSVcgDJEs0m5upXe8XJxwvQhYKKkCO6lrOCKwVCMc-xIZFYxYAZwdlIit8yoHrYxhgJvtbT64A9wrTSsx370ofkEz0KDKSqBsqLnh5MInpZ6cFqh1Z2yL1j39jp7q9u9ClkIEhUc1FxNXJjBp5jYWGdiTmTsuWbZJSVyTDgUL-2g1OffbB5XbY1q4S6jiQ9TkNJzd1__9rkg8frXPGvUUQXovAWNYvl7EoKvkCsVFTQsSMuntMnG7OpGguxMnb2824DrUg_zJq4iouIN4LA4qvTP_hDPcg=";
#     print(message_out == check)
    return '{}\n{}'.format(message,message_out)

@app.route('/blockchain')
def qrCodeApp():
    data = request.args.get('cc',None)
    return process_request(data)
    #return 'blockchain'


@app.route('/')
def hello_world2():
    return 'Service is awesome'

if __name__ == '__main__':
    app.run()
