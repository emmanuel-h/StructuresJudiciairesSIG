# StructuresJudiciairesSIG

<h2>Installation and configuration</h2>
<ul>
<li>
1. Create a directory to put the script coolScript and the FWTools installation from 2.a. It will also serve to store temporary files.
</li>
<li>
2. Install following the librairies :
	<ul>
	<li>
	a. FWTools (the jar is in the project root directory :<br>
		&emsp;tar xzvf FWTools-linux-2.0.6.tar.gz<br>
		&emsp;cd FWTools-linux-2.0.6<br>
		&emsp;./install.sh
	</li>
	<li>
	b. mapshaper. You must have Node.js on your computer, and not the old version in ubuntu packages. You can install the latest version with :<br>
		&emsp;curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash - <br>
		&emsp;sudo apt-get install -y nodejs) :<br>
	   To install mapshaper :<br>
		&emsp;git clone https://github.com/mbloch/mapshaper<br>
		&emsp;cd mapshaper<br>
		&emsp;npm install
	</li>
	<li>
	c. Install psycopg2 for the communication between python server and postgis database, for example with pip :<br>
		&emsp;sudo pip install psycopg2
	</li>
	</ul>
</li>
<li>
3. Modify the script entry (at the top of the file) to match your own configuration :
	<ul>
	<li>
	a. The three paths (to your tomcat, mapshaper and the directory to store files)
	</li>
	<li>
	b. The database informations (host, user, password and database name)
	</li>
	</ul>
</li>
</ul>
<h2>Servers setup</h2>
To launch the tomcat and refresh the json with the latest data, run coolScript with ./coolScript. To run the python server listening to the Personne update, run python server.py. The changes received will only be generated as JSon file when you write <em>reload</em> in the coolScript terminal. It allows you to verify the data.
