from flask import Flask
from flask import request
import os
import numpy as np

app = Flask(__name__)

port = int(os.getenv("VCAP_APP_PORT", 8000))

# Adam's Helper Functions
def makeLogisticMap(x0,r,dim1,dim2):
    mapSize = int((dim1*dim2)/8+1)
    myMap = [] #store as a flat list

    myMap.append(x0)# store first value
    for i in range(mapSize-1):
        myMap.append(r*myMap[i]*(1-myMap[i]))

    #print(type(myMap[1]))
    myMap = ['{:08b}'.format(int(item*255.0)) for item in myMap]
    #myMap = [int(item*255) for item in myMap] #debug, use this if you want to see the ints.
    return myMap

def stringToBitList(myString):
    splitString = myString.split('0x')
    splitClean = splitString[1:] #remove '' empty character from beginning
    splitBinary = [list('{:032b}'.format(int(item,16))) for item in splitClean]
    #print(len(splitBinary))
    listFlat = [int(number) for item in splitBinary for number in item]
    return listFlat
    #return np.reshape(np.array(listFlat),(32,32))

def detectPosition(myArray):
    #specify the corner anchors to find the QR code.
    myMask = np.array([[1,1,1,1,1,1,1],
                       [1,0,0,0,0,0,1],
                       [1,0,1,1,1,0,1],
                       [1,0,1,1,1,0,1],
                       [1,0,1,1,1,0,1],
                       [1,0,0,0,0,0,1],
                       [1,1,1,1,1,1,1]])

    # Index iterators
    i = 0
    j = 0
    output = []
    # This search should be made more efficient after first match is found...
    for i in range(26):
        for j in range(26):
            mySubset = myArray[i:(i+7),j:(j+7)]
            #print(mySubset)
            #print(myMask)
            #print("Row :" + str(i) + " Col: " + str(j))
            truthVal = np.array_equal(mySubset,myMask)
            if truthVal:
                output.append((i,j))
    return output

def fixRotation(targets,matrix):
    # grab target tuples
    target1 = targets[0]
    target2 = targets[1]
    target3 = targets[2]

    # case 1: 0 degrees, no rotation.
    if (target1[1] == target3[1]) and (target1[0] == target2[0]):
        #print("CASE 1")
        temp = matrix
        #detect new corners
        newTargs = detectPosition(temp)
        #calculate if 21 or 25
        size = newTargs[1][1]-newTargs[0][1]+7
        #grab top left corner index
        i, j = newTargs[0]
        slicedMatrix = temp[i:(i+size),j:(j+size)]
        return slicedMatrix, size

    # case 2: needs 270 ccw turn
    if target1[1] == target2[1]:
        #print("CASE 2")
        temp = np.rot90(matrix,3)
        #detect new corners
        newTargs = detectPosition(temp)
        #calculate if 21 or 25
        size = newTargs[1][1]-newTargs[0][1]+7
        #grab top left corner index
        i, j = newTargs[0]
        slicedMatrix = temp[i:(i+size),j:(j+size)]
        return slicedMatrix, size

    # case 3: needs 90 degree ccw turn
    if target2[1] == target3[1]:
        #print("CASE 3")
        temp = np.rot90(matrix,1)
        #detect new corners
        newTargs = detectPosition(temp)
        #calculate if 21 or 25
        size = newTargs[1][1]-newTargs[0][1]+7
        #grab top left corner index
        i, j = newTargs[0]
        slicedMatrix = temp[i:(i+size),j:(j+size)]
        return slicedMatrix, size

    # case 4: 180 degrees needed.
    if (target1[1] == target3[1]) and (target2[0] == target3[0]):
        #print("CASE 4")
        temp = np.rot90(matrix,2)
        #detect new corners
        newTargs = detectPosition(temp)
        #calculate if 21 or 25
        size = newTargs[1][1]-newTargs[0][1]+7
        #grab top left corner index
        i, j = newTargs[0]
        slicedMatrix = temp[i:(i+size),j:(j+size)]
        return slicedMatrix, size

def zigZag(size):
    #indexList = []
    if size == 25:
        zz25 = [(24, 24), (24, 23), (23, 24), (23, 23), (22, 24), (22, 23), (21, 24), (21, 23), (20, 24), (20, 23), (19, 24), (19, 23), (18, 24), (18, 23), (17, 24), (17, 23), (16, 24), (16, 23), (15, 24), (15, 23), (14, 24), (14, 23), (13, 24), (13, 23), (12, 24), (12, 23), (11, 24), (11, 23), (10, 24), (10, 23), (9, 24), (9, 23), (8, 24), (8, 23), (8, 22), (8, 21), (9, 22), (9, 21), (10, 22), (10, 21), (11, 22), (11, 21), (12, 22), (12, 21), (13, 22), (13, 21), (14, 22), (14, 21), (15, 22), (15, 21), (16, 22), (16, 21), (17, 22), (17, 21), (18, 22), (18, 21), (19, 22), (19, 21), (20, 22), (20, 21), (21, 22), (21, 21), (22, 22), (22, 21), (23, 22), (23, 21), (24, 22), (24, 21), (24, 20), (24, 19), (23, 20), (23, 19), (22, 20), (22, 19), (21, 20), (21, 19), (15, 20), (15, 19), (14, 20), (14, 19), (13, 20), (13, 19), (12, 20), (12, 19), (11, 20), (11, 19), (10, 20), (10, 19), (9, 20), (9, 19), (8, 20), (8, 19), (8, 18), (8, 17), (9, 18), (9, 17), (10, 18), (10, 17), (11, 18), (11, 17), (12, 18), (12, 17), (13, 18), (13, 17), (14, 18), (14, 17), (15, 18), (15, 17), (21, 18), (21, 17), (22, 18), (22, 17), (23, 18), (23, 17), (24, 18), (24, 17), (24, 16), (24, 15), (23, 16), (23, 15), (22, 16), (22, 15), (21, 16), (21, 15), (15, 16), (15, 15), (14, 16), (14, 15), (13, 16), (13, 15), (12, 16), (12, 15), (11, 16), (11, 15), (10, 16), (10, 15), (9, 16), (9, 15), (8, 16), (8, 15), (7, 16), (7, 15), (5, 16), (5, 15), (4, 16), (4, 15), (3, 16), (3, 15), (2, 16), (2, 15), (1, 16), (1, 15), (0, 16), (0, 15), (0, 14), (0, 13), (1, 14), (1, 13), (2, 14), (2, 13), (3, 14), (3, 13), (4, 14), (4, 13), (5, 14), (5, 13), (7, 14), (7, 13), (8, 14), (8, 13), (9, 14), (9, 13), (10, 14), (10, 13), (11, 14), (11, 13), (12, 14), (12, 13), (13, 14), (13, 13), (14, 14), (14, 13), (15, 14), (15, 13), (16, 14), (16, 13), (17, 14), (17, 13), (18, 14), (18, 13), (19, 14), (19, 13), (20, 14), (20, 13), (21, 14), (21, 13), (22, 14), (22, 13), (23, 14), (23, 13), (24, 14), (24, 13), (24, 12), (24, 11), (23, 12), (23, 11), (22, 12), (22, 11), (21, 12), (21, 11), (20, 12), (20, 11), (19, 12), (19, 11), (18, 12), (18, 11), (17, 12), (17, 11), (16, 12), (16, 11), (15, 12), (15, 11), (14, 12), (14, 11), (13, 12), (13, 11), (12, 12), (12, 11), (11, 12), (11, 11), (10, 12), (10, 11), (9, 12), (9, 11), (8, 12), (8, 11), (7, 12), (7, 11), (5, 12), (5, 11), (4, 12), (4, 11), (3, 12), (3, 11), (2, 12), (2, 11), (1, 12), (1, 11), (0, 12), (0, 11), (0, 10), (0, 9), (1, 10), (1, 9), (2, 10), (2, 9), (3, 10), (3, 9), (4, 10), (4, 9), (5, 10), (5, 9), (7, 10), (7, 9), (8, 10), (8, 9), (9, 10), (9, 9), (10, 10), (10, 9), (11, 10), (11, 9), (12, 10), (12, 9), (13, 10), (13, 9), (14, 10), (14, 9), (15, 10), (15, 9), (16, 10), (16, 9), (17, 10), (17, 9), (18, 10), (18, 9), (19, 10), (19, 9), (20, 10), (20, 9), (21, 10), (21, 9), (22, 10), (22, 9), (23, 10), (23, 9), (24, 10), (24, 9), (16, 8), (16, 7), (15, 8), (15, 7), (14, 8), (14, 7), (13, 8), (13, 7), (12, 8), (12, 7), (11, 8), (11, 7), (10, 8), (10, 7), (9, 8), (9, 7), (8, 8), (8, 7), (8, 5), (8, 4), (9, 5), (9, 4), (10, 5), (10, 4), (11, 5), (11, 4), (12, 5), (12, 4), (13, 5), (13, 4), (14, 5), (14, 4), (15, 5), (15, 4), (16, 5), (16, 4), (16, 3), (16, 2), (15, 3), (15, 2), (14, 3), (14, 2), (13, 3), (13, 2), (12, 3), (12, 2), (11, 3), (11, 2), (10, 3), (10, 2), (9, 3), (9, 2), (8, 3), (8, 2), (8, 1), (8, 0), (9, 1), (9, 0), (10, 1), (10, 0), (11, 1), (11, 0), (12, 1), (12, 0), (13, 1), (13, 0), (14, 1), (14, 0), (15, 1), (15, 0), (16, 1), (16, 0)]
        return zz25

    if size == 21:
        zz21 = [(20, 20),
                 (20, 19),
                 (19, 20),
                 (19, 19),
                 (18, 20),
                 (18, 19),
                 (17, 20),
                 (17, 19),
                 (16, 20),
                 (16, 19),
                 (15, 20),
                 (15, 19),
                 (14, 20),
                 (14, 19),
                 (13, 20),
                 (13, 19),
                 (12, 20),
                 (12, 19),
                 (11, 20),
                 (11, 19),
                 (10, 20),
                 (10, 19),
                 (9, 20),
                 (9, 19),
                 (8, 20),
                 (8, 19),
                 (8, 18),
                 (8, 17),
                 (9, 18),
                 (9, 17),
                 (10, 18),
                 (10, 17),
                 (11, 18),
                 (11, 17),
                 (12, 18),
                 (12, 17),
                 (13, 18),
                 (13, 17),
                 (14, 18),
                 (14, 17),
                 (15, 18),
                 (15, 17),
                 (16, 18),
                 (16, 17),
                 (17, 18),
                 (17, 17),
                 (18, 18),
                 (18, 17),
                 (19, 18),
                 (19, 17),
                 (20, 18),
                 (20, 17),
                 (20, 16),
                 (20, 15),
                 (19, 16),
                 (19, 15),
                 (18, 16),
                 (18, 15),
                 (17, 16),
                 (17, 15),
                 (16, 16),
                 (16, 15),
                 (15, 16),
                 (15, 15),
                 (14, 16),
                 (14, 15),
                 (13, 16),
                 (13, 15),
                 (12, 16),
                 (12, 15),
                 (11, 16),
                 (11, 15),
                 (10, 16),
                 (10, 15),
                 (9, 16),
                 (9, 15),
                 (8, 16),
                 (8, 15),
                 (8, 14),
                 (8, 13),
                 (9, 14),
                 (9, 13),
                 (10, 14),
                 (10, 13),
                 (11, 14),
                 (11, 13),
                 (12, 14),
                 (12, 13),
                 (13, 14),
                 (13, 13),
                 (14, 14),
                 (14, 13),
                 (15, 14),
                 (15, 13),
                 (16, 14),
                 (16, 13),
                 (17, 14),
                 (17, 13),
                 (18, 14),
                 (18, 13),
                 (19, 14),
                 (19, 13),
                 (20, 14),
                 (20, 13),
                 (20, 12),
                 (20, 11),
                 (19, 12),
                 (19, 11),
                 (18, 12),
                 (18, 11),
                 (17, 12),
                 (17, 11),
                 (16, 12),
                 (16, 11),
                 (15, 12),
                 (15, 11),
                 (14, 12),
                 (14, 11),
                 (13, 12),
                 (13, 11),
                 (12, 12),
                 (12, 11),
                 (11, 12),
                 (11, 11),
                 (10, 12),
                 (10, 11),
                 (9, 12),
                 (9, 11),
                 (8, 12),
                 (8, 11),
                 (7, 12),
                 (7, 11),
                 (5, 12),
                 (5, 11),
                 (4, 12),
                 (4, 11),
                 (3, 12),
                 (3, 11),
                 (2, 12),
                 (2, 11),
                 (1, 12),
                 (1, 11),
                 (0, 12),
                 (0, 11),
                 (0, 10),
                 (0, 9),
                 (1, 10),
                 (1, 9),
                 (2, 10),
                 (2, 9),
                 (3, 10),
                 (3, 9),
                 (4, 10),
                 (4, 9),
                 (5, 10),
                 (5, 9),
                 (7, 10),
                 (7, 9),
                 (8, 10),
                 (8, 9),
                 (9, 10),
                 (9, 9),
                 (10, 10),
                 (10, 9),
                 (11, 10),
                 (11, 9),
                 (12, 10),
                 (12, 9),
                 (13, 10),
                 (13, 9),
                 (14, 10),
                 (14, 9),
                 (15, 10),
                 (15, 9),
                 (16, 10),
                 (16, 9),
                 (17, 10),
                 (17, 9),
                 (18, 10),
                 (18, 9),
                 (19, 10),
                 (19, 9),
                 (20, 10),
                 (20, 9),
                 (12, 8),
                 (12, 7),
                 (11, 8),
                 (11, 7),
                 (10, 8),
                 (10, 7),
                 (9, 8),
                 (9, 7),
                 (8, 8),
                 (8, 7),
                 (8, 5),
                 (8, 4),
                 (9, 5),
                 (9, 4),
                 (10, 5),
                 (10, 4),
                 (11, 5),
                 (11, 4),
                 (12, 5),
                 (12, 4),
                 (12, 3),
                 (12, 2),
                 (11, 3),
                 (11, 2),
                 (10, 3),
                 (10, 2),
                 (9, 3),
                 (9, 2),
                 (8, 3),
                 (8, 2),
                 (8, 1),
                 (8, 0),
                 (9, 1),
                 (9, 0),
                 (10, 1),
                 (10, 0),
                 (11, 1),
                 (11, 0),
                 (12, 1),
                 (12, 0)]

        return zz21

def bitUnravelDecode(indices, matrix):
    bitList = []
    for pair in indices:
        bitList.append(matrix[pair])

    #remove padding - almost there!

    # determine size of message.
    msgSize = "".join([str(item) for item in bitList[0:8]])
    msgSize = int(msgSize,2)
    # calculate bits to keep!
    numBits = (2*msgSize+1)*8

    string = []
    for i in range(8,numBits,16):
        character = bitList[i:(i+8)]
        #checksum = bitList[(i+8):(i+16)] #ignore ecc for now.
        character = "".join([str(item) for item in character])
        if character != '':
            string.append(chr(int(character,2)))

    return "".join(string)

def make_binary_string(string, total_bits):
    binary_string = '{0:08b}'.format(len(string))
    for i in string:
        binary = format(ord(i), '08b')
        binary_string += binary
        if binary.count("1") % 2 == 1:
            binary_string += '{0:08b}'.format(1)
        else:
            binary_string += '{0:08b}'.format(0)
    extra_chars = '1110110000010001'
    num_copies = ((total_bits - len(binary_string)) // 16) + 1
    binary_string += extra_chars * num_copies
    binary_string = binary_string[0:total_bits]
    assert len(binary_string) == total_bits
    return binary_string

def makeLogisticMap_bryce(x0,r,dim1,dim2):
    mapSize = int((dim1*dim2)/8+1)
    myMap = [] #store as a flat list

    myMap.append(x0)# store first value
    for i in range(mapSize-1):
        myMap.append(r*myMap[i]*(1-myMap[i]))

    myMap = ['{:08b}'.format(int(item*255.0)) for item in myMap]
    return myMap

def stringToBitList_bryce(myString):
    splitString = myString.split('0x')
    splitClean = splitString[1:] #remove '' empty character from beginning
    splitBinary = [list('{:032b}'.format(int(item,16))) for item in splitClean]
    listFlat = [int(number) for item in splitBinary for number in item]
    return listFlat

def main_encode(string):
    if len(string) <= 13:
        mode = 21
        total_bits = 224
    elif len(string) <= 22:
        mode = 25
        total_bits = 370

    corner = np.array([[ 1,  1,  1,  1,  1, 1, 1, 0],
           [ 1, 0, 0, 0, 0, 0, 1, 0],
           [ 1, 0, 1, 1, 1, 0, 1, 0],
           [ 1, 0, 1, 1, 1, 0, 1, 0],
           [ 1, 0, 1, 1, 1, 0, 1, 0],
           [ 1, 0, 0, 0, 0, 0, 1, 0],
           [ 1, 1, 1, 1, 1, 1, 1, 0],
           [ 0, 0, 0, 0, 0, 0, 0, 0]])
    small_anchor = np.array([[ 1,  1,  1,  1,  1],
           [ 1, 0, 0, 0, 1],
           [ 1, 0, 1, 0, 1],
           [ 1, 0, 0, 0, 1],
           [ 1,  1,  1,  1,  1]])
    line = np.array([[1, 0, 1, 0, 1]])
    line25 = np.array([[1, 0, 1, 0, 1, 0, 1, 0, 1]])

    binary_string = make_binary_string(string, total_bits)

    if mode == 21:
        mat = np.zeros((21,21))
        mat[0:8,0:8] = corner
        mat[0:8,-8:] = np.fliplr(corner)
        mat[-8:,0:8] = np.flipud(corner)
        mat[6, 8:13] = line
        mat[8:13, 6] = line
        mat = mat.astype(int)
    if mode == 25:
        mat = np.zeros((25,25))
        mat[0:8,0:8] = corner
        mat[0:8,-8:] = np.fliplr(corner)
        mat[-8:,0:8] = np.flipud(corner)
        mat[16:21,16:21] = small_anchor
        mat[6, 8:17] = line25
        mat[8:17, 6] = line25
        mat = mat.astype(int)

    indices = zigZag(mode)
    assert(len(indices) == len(binary_string))
    index_and_bits = zip(indices, binary_string)
    for index, bit in index_and_bits:
        mat[index[0], index[1]] = bit

    mat = mat.flatten()

    step = 32
    counted = 0
    hex_string = ''
    while counted < len(mat):
        chunk = mat[counted:counted+step]
        temp = ''
        for elem in chunk:
            temp += str(elem)
        hex_string += hex(int(temp, 2))
        counted += step
    myMapInvFlat = [1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0]
    myBitList = stringToBitList_bryce(hex_string)
    test = list(mat)
    myXORList = [test[i]^myMapInvFlat[i] for i in range(len(test))]

    step = 32
    counted = 0
    conv_hex_string = ''
    while counted < len(mat):
        if counted + step > len(mat):
            chunk = myXORList[counted:len(mat)]
        else:
            chunk = myXORList[counted:counted+step]
        temp = ''
        for elem in chunk:
            temp += str(elem)
        conv_hex_string += hex(int(temp, 2))
        counted += step

    return conv_hex_string

@app.route('/qrcode')
def qrCodeApp():
    type = request.args.get('type',"encode")
    if type == 'decode':
        decode_flag = 1
    else:
        decode_flag = 0
    #print(decode_flag)
    #print(type)
    data = request.args.get('data',"CC Team")


    # Adam's Code
    if decode_flag:
        myMapInvFlat = [1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0]
        myBitList = stringToBitList(data)

        myXORList = [myBitList[i]^myMapInvFlat[i] for i in range(len(myBitList))]

        arrayDim = int(np.sqrt(len(myBitList)))

        myXORList = myXORList[0:(arrayDim*arrayDim)]
        qrArray = np.reshape(np.array(myXORList),(arrayDim,arrayDim))

        targets = detectPosition(qrArray)
        #print(targets) #debug
        #check length of targets
        #print(len(targets)) #debug

        #determine the rotation, fix it, slice the matrix.
        adjustedMatrix, size = fixRotation(targets,qrArray)

        #grab the unraveledIndices
        unraveledIndices = zigZag(size)

        #apply these indices to the matrix
        message = bitUnravelDecode(unraveledIndices,adjustedMatrix)
        #print(message)
        return message
    else:
        string = data
        hex_string = main_encode(string)
        return hex_string

@app.route('/')
def hello_world2():
    return 'Service is running'

if __name__ == '__main__':
    app.run(threaded=True)
