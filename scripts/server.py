'''
    Simple socket server using threads
'''

import socket
import sys
import psycopg2
from thread import *

HOST = ''   # Symbolic name meaning all available interfaces
PORT = 2412 # Arbitrary non-privileged port

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print 'Socket created'

#Bind socket to local host and port
try:
    s.bind((HOST, PORT))
except socket.error as msg:
    print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
    sys.exit()

print 'Socket bind complete'

#Start listening on socket
s.listen(10)
print 'Socket now listening'

#Function for handling connections. This will be used to create threads
def clientthread(conn):
    #Sending message to connected client
    conn.send('OK') #send only takes string

    #infinite loop so that function do not terminate and thread do not end.
    print "Begin recv"
    #Receiving from client
    data = conn.recv(1024)
    #reply = 'OK...' + data

    print data
    separateData=data.split("&")
    separateData.pop(0)

    for sData in separateData:
        print sData
    conn = psycopg2.connect(dbname="StructureJudiciaire", user="postgres", password="postgres", host="localhost", port="5432")
    curs = conn.cursor()
    # Table 'my_points' has a geography column 'geog'
    curs.execute("INSERT INTO public.personne (longitude, latitude, nom, prenom, adresse, telephone, profession,geom) VALUES (%s,%s,%s,%s,%s,%s,%s,ST_SetSRID(ST_MakePoint(cast(%s as numeric),cast(%s as numeric)),2154))",([separateData[0],separateData[1],separateData[2],separateData[3],separateData[4],separateData[5],separateData[6],separateData[0],separateData[1]]))
    conn.commit()
    conn.close()

#now keep talking with the client
while 1:
    #wait to accept a connection - blocking call
    conn, addr = s.accept()
    print 'Connected with ' + addr[0] + ':' + str(addr[1])

    #start new thread takes 1st argument as a function name to be run, second is the tuple of arguments to the function.
    start_new_thread(clientthread ,(conn,))

s.close()
