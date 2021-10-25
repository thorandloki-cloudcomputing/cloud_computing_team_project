package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.lang.Math;
import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
@SpringBootApplication
@RestController
public class Application {
    static long rsa_constant = 1561906343821L;
    static long my_public_key = 1097844002039L; // referred to as e in the write-up
    static long my_private_key = 343710770439L;
    static String AWS_acct = "922823096026"; //# Team ThorAndLoki AWS account number
    static long payment_constant = 500000000L;
    static long prev_time_storage = 0L;
    static String previous_hash_class = "";

    //Cite: https://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
    public static String compute_sha(String input) throws NoSuchAlgorithmException {
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      md.update(input.getBytes(StandardCharsets.UTF_8));
      byte[] digest = md.digest();

      String hex = String.format("%064x", new BigInteger(1, digest));
      //System.out.println(hex); //debug
      return hex;
    }

    public static String compute_cchash(String input) throws NoSuchAlgorithmException {
      String sha = compute_sha(input);
      return sha.substring(0,8);
    }

    public static String proof_of_work_hash(String input, String pow) throws NoSuchAlgorithmException {
      String sha = compute_sha(input);
      String hash_out = compute_cchash(sha+pow);
      return hash_out;
    }


    //Cite: https://www.geeksforgeeks.org/biginteger-modpow-method-in-java/
    public static Long RSA_encrypt(Long message, Long public_key) {
          BigInteger biginteger1, biginteger2, result;
          biginteger1 = BigInteger.valueOf(message);
          biginteger2 = BigInteger.valueOf(rsa_constant);
          BigInteger exponent = BigInteger.valueOf(public_key);
          result = biginteger1.modPow(exponent, biginteger2);
          return result.longValue();
    }

    public static Long RSA_decrypt(Long cipher_text, Long private_key) {
          BigInteger biginteger1, biginteger2, result;
          biginteger1 = BigInteger.valueOf(cipher_text);
          biginteger2 = BigInteger.valueOf(rsa_constant);
          BigInteger exponent = BigInteger.valueOf(private_key);
          result = biginteger1.modPow(exponent, biginteger2);
          return result.longValue();
    }

    public static int hash_comparison(String given, String computed){
      if (given.equals(computed)){
        return 0;
      }
      return -1;
    }

    public static int process_transaction(JsonObject input)  throws NoSuchAlgorithmException {
      //Assign everything a default value...
      String given_hash = "";
      String time = "";
      String sender = "";
      String receiver = "";
      String amt = "";
      String fee = "";

      if (input.get("hash") != null){
        given_hash = input.get("hash").getAsString();
      }
      if (input.get("time") != null){
        time = input.get("time").getAsString();
      }
      if (input.get("send") != null){
        sender = input.get("send").toString();
      }
      if (input.get("recv") != null){
        receiver = input.get("recv").toString();
      }
      if (input.get("amt") != null){
        amt = input.get("amt").toString();
      }
      if (input.get("fee") != null){
        fee = input.get("fee").toString();
      }
      String myString = time + "|" + sender + "|" + receiver + "|" + amt + "|" + fee;
      String computed_hash = compute_cchash(myString);
      //System.out.println("Process Transaction Debug");
      //System.out.println(myString);
      //System.out.println(computed_hash);
      //System.out.println(given_hash);

      //Convert this string to hexadecimal
      return hash_comparison(given_hash,computed_hash);

    }

    public static int verify_sig(JsonObject input) {
      Long signature = null;
      if (input.get("sig") != null){
        signature = input.get("sig").getAsLong();
      }

      if (signature != null) {
        String given_hash = input.get("hash").getAsString();
        Long computed_hash = RSA_encrypt(signature,input.get("send").getAsLong());
        String hex_hash = String.format("%1$08x",computed_hash);
        return hash_comparison(given_hash,hex_hash);
        //return hex_hash + " " + given_hash;
      }
      return 0;
    }

    public static Long [] extract_data(JsonObject input) {
      Long payment = 0L;
      Long fee = 0L;
      Long sender = 0L;
      Long receiver = 0L;
      Long [] output = new Long[4];

      if (input.get("amt") != null){
        payment = input.get("amt").getAsLong();
      }
      if (input.get("fee") != null){
        fee = input.get("fee").getAsLong();
      }
      if (input.get("send") != null){
        sender = input.get("send").getAsLong();
      }
      if (input.get("recv") != null){
        receiver = input.get("recv").getAsLong();
      }
      output[0] = payment;
      output[1] = fee;
      output[2] = sender;
      output[3] = receiver;

      return output;
    }

    public static Long [] check_time(Long newer, Long older) {
      Long [] output = new Long[2];
      if (newer <= older){
        output[0] = -1L;
        output[1] = -1L;
        return output;
      }
      output[0] = newer;
      output[1] = 0L;
      return output;
    }

    public static int check_hash(JsonObject input)  throws NoSuchAlgorithmException {
      if(process_transaction(input) != 0){
        return -1;
      }
      return 0;
    }

    public static int check_positive(int val){
      if (val < 0) {
        return -1;
      }
      return 0;
    }

    public static int check_signature(JsonObject transaction){
      if (verify_sig(transaction) != 0){
        return -1;
      }
      return 0;
    }

    //This could cause an issue...
    public static JsonObject check_balances(JsonObject transaction, JsonObject user_balances){
      //System.out.println("Check Balances");
      //System.out.println(new Gson().toJson(transaction));
      //System.out.println(new Gson().toJson(user_balances));
      Long [] extracted_data = extract_data(transaction);
      //System.out.println(extracted_data);
      Long payment = extracted_data[0];
      Long fee = extracted_data[1];
      Long sender = extracted_data[2];
      Long receiver = extracted_data[3];

      if (receiver!=0L){
        //Typecast sender/receiver to a string...
        String receiver_string = String.valueOf(receiver);
        Long old_balance = 0L;
        if(user_balances.get(receiver_string) != null){
          old_balance = user_balances.get(receiver_string).getAsLong();
        }
        Long temp = old_balance + payment;
        user_balances.addProperty(receiver_string,temp);
      }
      if (sender!=0L){
        //Typecast sender to a string...
        String sender_string = String.valueOf(sender);
        Long old_balance2 = 0L;
        if(user_balances.get(sender_string) != null){
          old_balance2 = user_balances.get(sender_string).getAsLong();
        }
        Long temp2 = old_balance2 - (payment+fee);
        if (temp2 < 0L){
          //System.out.println("Negative Balance Issue");
          //Negative balance remaining
          JsonObject remaining = new JsonObject();
          return remaining;

        }
        user_balances.addProperty(sender_string,temp2);
      }
      //System.out.println("Resulting Balances");
      //System.out.println(new Gson().toJson(user_balances));
      return user_balances;
    }

    public static Long [] check_block_id(Long newer, Long older){
      Long [] output = new Long[2];
      if ((newer-older) != 1){
        output[0] = -1L;
        output[1] = -1L;
        return output;
      }
      output[0] = newer;
      output[1] = 1L;
      return output;
    }

    public static int check_proof_of_work(String working_pow, JsonObject block)   throws NoSuchAlgorithmException{
      String pow = "0";
      String hash = "0";
      if( block.get("pow") != null ){
        pow = block.get("pow").getAsString();
      }
      if( block.get("hash") != null){
        hash = block.get("hash").getAsString();
      }
      if ( !proof_of_work_hash(working_pow, pow).equals(hash)){
        //System.out.println(proof_of_work_hash(working_pow, pow));
        //System.out.println(hash);
        return -1;
      }
      return 0;
    }

    public static int check_payment(int prev_block_ID, Long reward){
      Long payment = payment_constant;
      //This might be suspcious during debugging...
      for (int i = 0; i < (prev_block_ID/2); i++){
        payment = payment/2L;
      }
      // System.out.println(payment);
      // System.out.println(reward);

      if ( !payment.equals(reward) ){
        return -1;
      }
      return 0;
    }

    public static JsonObject transaction_error_checker(JsonObject transaction, JsonObject user_balances, Long new_time, Long prev_time) throws NoSuchAlgorithmException{

      if(check_hash(transaction) != 0){
        //System.out.println(new Gson().toJson(transaction));
        //System.out.println("Tx Error Check 1");
        return new JsonObject();
      }
      int fee = 0;
      if (transaction.get("fee") != null ){
        fee = transaction.get("fee").getAsInt();
      }
      if(check_positive(fee) != 0){
        //System.out.println("Tx Error Check 2");
        return new JsonObject();
      }
      int amt = 0;
      if (transaction.get("amt") != null ){
        amt = transaction.get("amt").getAsInt();
      }
      if(check_positive(amt) != 0){
        //System.out.println("Tx Error Check 3");
        return new JsonObject();
      }
      if (check_signature(transaction) != 0){
        //System.out.println("Tx Error Check 4");
        return new JsonObject();
      }
      Long [] timeInfo = check_time(new_time,prev_time);
      if (timeInfo[0].equals(-1L)){
        //System.out.println("Tx Error Check 5");
        return new JsonObject();
      }
      Long prevTime = timeInfo[0];
      JsonObject user_balances_new = check_balances(transaction, user_balances);
      //Check for error
      if (user_balances_new.equals(new JsonObject())){
        //System.out.println("User Balances New is Faulty");
        //System.out.println("Tx Error Check 6");
        return new JsonObject();
      }

      prev_time_storage = prevTime; //Store this somewhere so we don't have to return it..
      return user_balances_new;
    }

    public static Long [] validate_request(JsonObject request) throws NoSuchAlgorithmException{
      Long [] funOut = new Long[3];
      JsonArray chain = request.getAsJsonArray("chain");
      JsonArray new_transactions = request.getAsJsonArray("new_tx");
      JsonArray full_list_of_transactions = new JsonArray();
      JsonObject user_balances = new JsonObject();
      String prev_hash = "00000000";
      Long prev_time = 0L;
      int prev_block_ID = -1;

      for (int i = 0; i < chain.size(); i++){
        JsonObject block = chain.get(i).getAsJsonObject();
        String working_pow = block.get("id").toString();
        working_pow += "|"+prev_hash;
        int minerReward = 0;
        JsonArray transactionList = block.getAsJsonArray("all_tx");

        for (int j = 0; j < transactionList.size(); j++ ){
          JsonElement transaction = transactionList.get(j);
          if (!(full_list_of_transactions.contains(transaction))){
            //System.out.println("new element");
            //Add to the list..
            full_list_of_transactions.add(transaction);
          } else {
            funOut[0] = -1L;
            return funOut;
          }
          JsonObject transObj = transactionList.get(j).getAsJsonObject();
          try {
            Long new_time = 0L;
            if(transObj.get("time") != null){
              new_time = transObj.get("time").getAsLong();
            }
            user_balances = transaction_error_checker(transObj, user_balances,new_time, prev_time);
            //System.out.println("User_balances after return to validate");
            //System.out.println(new Gson().toJson(user_balances));
            prev_time = prev_time_storage; //Grab from class variable space.
            //System.out.println(prev_time);
            //System.out.
          }
          catch(Exception e) {
            funOut[0] = -1L;
            return funOut;
          }
          working_pow += "|" + transObj.get("hash").getAsString();
          //System.out.println(working_pow);
        }
        //System.out.println("Moving On");
        JsonObject lastTrans = transactionList.get(transactionList.size()-1).getAsJsonObject();

        if (check_proof_of_work(working_pow, block) != 0){
          funOut[0] = -1L;
          return funOut;
        }
        Long currentBID = -1L;
        if (block.get("id")!=null){
          currentBID = block.get("id").getAsLong();
        }

        Long [] cbid = check_block_id(currentBID,(long)prev_block_ID);
        if (cbid[1].equals(-1L)){
          //System.out.println("BLOCK ID FAILED");
          funOut[0] = -1L;
          return funOut;
        }
        prev_block_ID = cbid[0].intValue();
        //System.out.println("PREVIOUS BLOCK ID");
        //System.out.println(prev_block_ID); // debug

        prev_hash = block.get("hash").getAsString();
        Long reward = 0L;
        if( lastTrans.get("amt") != null){
          reward = lastTrans.get("amt").getAsLong();
        }
        if (check_payment(prev_block_ID,reward)!=0){
          //System.out.println("BLOCK ID FAILED");
          funOut[0] = -1L;
          return funOut;
        }
      }
      //System.out.println("Control reaches here");
      for (int i = 0; i < new_transactions.size(); i++){
        JsonObject newTrans = new_transactions.get(i).getAsJsonObject();
        Long new_time = prev_time + 1L;
        if(newTrans.get("time")!= null){
          new_time = newTrans.get("time").getAsLong();
        }
        if (new_time <= prev_time){
        //  System.out.println("Offending line");
          funOut[0] = -1L;
          return funOut;
        } else {
          prev_time = new_time;
        }
        if (verify_sig(newTrans) != 0) {
        //  System.out.println("Offending 2");
          funOut[0] = -1L;
          return funOut;
        }
        user_balances = check_balances(newTrans, user_balances);
        if(user_balances.equals(new JsonObject())){
        //  System.out.println("Offending 3");
          funOut[0] = -1L;
          return funOut;
        }
      }
      funOut[0] = 0L;
      funOut[1] = (long)prev_block_ID;
      funOut[2] = prev_time;

      //Set a class variable here...
      previous_hash_class = prev_hash;

      return funOut;

    }

    public static String [] mine_for_int(String pow_string, String new_target) throws NoSuchAlgorithmException{
      //System.out.println("HELLO FROM INT MINING");
      //System.out.println(new_target);
      String [] outputStrings = new String[2];
      Long pow_val = 0L;
      String pow_val_string = String.valueOf(pow_val);

      String result = proof_of_work_hash(pow_string, pow_val_string);
      //Long result_long = Long.parseLong(result);
      //Long new_target_long = Long.parseLong(new_target);
      //System.out.println("Reaches this loop");
      //Long result_hex = Long.decode("0x"+result);
      //Long new_target_hex = Long.decode("0x"+new_target);
      while (result.compareTo(new_target) >= 0){
      //while (result_hex >= new_target_hex){
        pow_val += 1L;
        pow_val_string = String.valueOf(pow_val);
        result = proof_of_work_hash(pow_string, pow_val_string);
        //result_hex = Long.decode("0x"+result);
        //result_long = Long.parseLong(result);
      }
      //System.out.println("Reaches after loop");
      outputStrings[0] = pow_val_string;
      outputStrings[1] = result;

      return outputStrings;

    }

    public static JsonObject generate_response(JsonObject request, String prev_hash, int prev_block_ID, Long new_time) throws NoSuchAlgorithmException{
      JsonArray new_transactions = request.getAsJsonArray("new_tx");
      String new_target = request.get("new_target").getAsString();
      JsonArray transaction_list = new JsonArray();
      String [] hash_list = new String[500];
      int hash_list_ctr = 0;
      for (int i = 0; i < new_transactions.size(); i++){
        JsonObject currentTrans = new_transactions.get(i).getAsJsonObject();
        String transHash = "0";
        if (currentTrans.get("hash")!= null){
          transHash = currentTrans.get("hash").getAsString();
        }
        if (transHash.equals("0")){
          JsonObject new_transaction = new JsonObject();
          String myString = currentTrans.get("time").getAsString()+"|"+String.valueOf(my_public_key)+"|"+currentTrans.get("recv").toString()+"|"+currentTrans.get("amt").toString()+"|"+"0";
          //System.out.println("LOOK RIGHT HERE");
          //System.out.println(myString);
          String hash_val = compute_cchash(myString);
          //System.out.println(hash_val);
          new_transaction.addProperty("amt",currentTrans.get("amt").toString());
          new_transaction.addProperty("fee",0);
          new_transaction.addProperty("hash",hash_val);
          new_transaction.addProperty("recv",currentTrans.get("recv").toString());
          new_transaction.addProperty("send",my_public_key);
          Long hash_val_x16 = Long.decode("0x"+hash_val);
          new_transaction.addProperty("sig",RSA_decrypt(hash_val_x16,my_public_key));
          new_transaction.addProperty("time",currentTrans.get("time").getAsString());
          currentTrans = new_transaction;
          //System.out.println(new Gson().toJson(new_transaction));
        }
        transaction_list.add(currentTrans);
        hash_list[hash_list_ctr] = currentTrans.get("hash").getAsString();
        hash_list_ctr++; //This is janky.
      }
      int new_block_id = prev_block_ID +1;
      //Compute reward...
      JsonObject reward = new JsonObject();
      String reward_time = String.valueOf(new_time+600000000000L);
      Long payment = payment_constant;
      for (int k = 0; k < new_block_id/2; k++){
        payment = payment / 2L;
      }
      String myString = reward_time+"|"+"|"+String.valueOf(my_public_key)+"|"+String.valueOf(payment)+"|";
      //System.out.println(myString);
      String reward_hash = compute_cchash(myString);
      reward.addProperty("amt",payment);
      //new_transaction.addProperty("fee",0);
      reward.addProperty("hash",reward_hash);
      reward.addProperty("recv",my_public_key);
      reward.addProperty("time",reward_time);
      //System.out.println(new Gson().toJson(reward));
      //Add it to transaction List
      transaction_list.add(reward);
      hash_list[hash_list_ctr] = reward.get("hash").getAsString();
      hash_list_ctr++;
      // for (int l = 0; l < hash_list_ctr; l++) {
      //   System.out.println(hash_list[l]);
      // }

      String pow_string = String.valueOf(new_block_id)+"|"+prev_hash;
      for (int l = 0; l < hash_list_ctr; l++) {
        pow_string += "|" + hash_list[l];
      }
      //System.out.println(pow_string);
      //System.out.println(new_target);
      String [] mining_results = mine_for_int(pow_string, new_target);
      String pow_val = mining_results[0];
      String result = mining_results[1];
      //Crete new block
      JsonObject new_block = new JsonObject();
      new_block.add("all_tx",transaction_list);
      new_block.addProperty("hash",result);
      new_block.addProperty("id",new_block_id);
      new_block.addProperty("pow",pow_val);
      new_block.addProperty("target",new_target);

      JsonArray chain = request.getAsJsonArray("chain");
      chain.add(new_block);
      JsonObject out = new JsonObject();
      out.add("chain",chain);

      //System.out.println(new Gson().toJson(out));
      return out;
    }







    public static byte[] urlsafe_b64decode(String input){
      byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
      //String decodedInput = new String(decodedBytes);
      return decodedBytes;
    }

    //Cite: https://stackoverflow.com/questions/33020765/java-decompress-a-string-compressed-with-zlib-deflate
    public static String zlibDecompress(byte [] input) throws Exception {

      ByteArrayInputStream bais = new ByteArrayInputStream(input);
      InflaterInputStream iis = new InflaterInputStream(bais);

      String result = "";
      byte[] buf = new byte[5];
      int rlen = -1;
      while ((rlen = iis.read(buf)) != -1) {
          result += new String(Arrays.copyOf(buf, rlen));
        }
      return result;
    }

    public static String doBitcoin(String request) throws NoSuchAlgorithmException, Exception, IOException {
      String message = "ThorAndLoki" + "," + AWS_acct;
      JsonObject data = new JsonObject();
      Long [] valid_data = new Long[3];
      try {
        String query = request;
        String output = zlibDecompress(urlsafe_b64decode(query));
        data = new JsonParser().parse(output).getAsJsonObject();
        //String test = data.get("chain").toString();
        //return output;
      }
      catch(Exception e) {
        String message_out = message + "\n"+ "INVALID | Exception 1";
        //Add a return statement here
        return message_out;
      }
      try {
        valid_data = validate_request(data);
      }
      catch(Exception e) {
        String message_out = message + "\n"+ "INVALID | Exception 2";
        return message_out;
      }
      JsonObject response = generate_response(data, previous_hash_class, valid_data[1].intValue(), valid_data[2]);
      String jsonString = new Gson().toJson(response);
      //System.out.println("Current Test Output");
      //System.out.println(jsonString);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DeflaterOutputStream dos = new DeflaterOutputStream(baos);
      dos.write(jsonString.getBytes());
      dos.flush();
      dos.close();

      String finalOut = Base64.getUrlEncoder().encodeToString(baos.toByteArray());
      //System.out.println(finalOut);
      return finalOut;
    }

  @RequestMapping("/")
  public String home() throws NoSuchAlgorithmException, Exception, IOException {
    return "running";

  }

  @RequestMapping(value="/blockchain", method=RequestMethod.GET)
  public String qrcode(@RequestParam(required = false, name = "cc",defaultValue = "missing") String type)  throws NoSuchAlgorithmException, Exception, IOException {
    if (!type.equals("missing")){
      //String testString = "eJyFk9tum0AQht9lr7mYw85heZWqigBDbClxqxg1lSK_ewcCBddJuldoF3a--f7hLXXH5nRO9be31Dw9PYy_58eXvvuVai-SRd3Ji1WpeR5TLbCsKo2n5z7VCcVJCDJsK1Xp2FyOcZhbMnOFdP1epZ8_XmNrOj0dUg1_XwLrilu2OBibl8d-nG5N1-qG6HJ6TDUKcRCwBVepVkpGViRAp9gb-oCiDL4AI6lnL1nvgKc7TAiLaVS-9OfDvx0vfB1k46Y9zEjvNZUwxx1MbOU_ZhCA0OMGl4KbmaYVHfDgOzMEtLjBzQ2IDAeUvRv4WE60D0ScobCtbgqIFQgHBVc35kiyMGc3LYT3xIWyUg4g2dTcaF7z1VZx8H4GmimoCDmia5kieKdAKOY5JiSMrRiwIDgbSfF7BlSPtDFagq_jabXDFmHYxXPb9lyH5JN4FBhIVQ2Ut3haMAn1sosH13Rolw65N-w36TTzN-f-9WHbg2m45639NEfpQgaCRKsqFVcjN2bgxRQL69IEZ1K2fCdLWZkMgxf168BwaLN3hjtZH_zln9bJYjm7ksI0uNc_g4cQeA==";
      String myOutput = doBitcoin(type);
      return myOutput;
    }
    return "No params found :(";

  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
