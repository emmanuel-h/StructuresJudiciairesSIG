'''
    Simple socket server using threads
'''

import socket
import sys
import psycopg2
from thread import *

HOST = ''
PORT = 2512

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print 'Socket created'

# Bind socket to local host and port
try:
    s.bind((HOST, PORT))
except socket.error as msg:
    print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
    sys.exit()

print 'Socket bind complete'

# Start listening on socket
s.listen(10)
print 'Socket now listening'

def clientthread(conn):
    # Sending message to connected client
    conn.send('OK') #send only takes string

    print "Begin recv"
    # Receiving from client
    data = conn.recv(1024)

    print data
    # Split the data and remove the header
    separateData=data.split("&")
    separateData.pop(0)

	# Connect to the database and push the changes
    conn = psycopg2.connect(dbname="StructureJudiciaire", user="postgres", password="postgres", host="localhost", port="5432")
    curs = conn.cursor()
    curs.execute("INSERT INTO public.personne (longitude, latitude, nom, prenom, adresse, telephone, profession,geom) VALUES (%s,%s,%s,%s,%s,%s,%s,ST_SetSRID(ST_MakePoint(cast(%s as numeric),cast(%s as numeric)),2154))",([separateData[0],separateData[1],separateData[2],separateData[3],separateData[4],separateData[5],separateData[6],separateData[0],separateData[1]]))
    conn.commit()
    conn.close()

while 1:
    # Wait to accept a connection
    conn, addr = s.accept()
    print 'Connected with ' + addr[0] + ':' + str(addr[1])
	# Each new connection is handled with a new thread
    start_new_thread(clientthread ,(conn,))

s.close()
