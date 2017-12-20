# StructuresJudiciairesSIG

<h2>To deploy the server</h2>
<ul>
<li>
1. Create a directory to put the script coolScript and the FWTools installation from 2.a. It will also serve to store temporary files.
</li>
<li>
2. Install following the librairies :
	<ul>
	<li>
	a. FWTools (the jar is in the project root directory :<br>
		tar xzvf FWTools-linux-2.0.6.tar.gz<br>
		cd FWTools-linux-2.0.6<br>
		./install.sh
	</li>
	<li>
	b. mapshaper. You must have Node.js on your computer, and not the old version in ubuntu packages. You can install the latest version with :<br>
		curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash - <br>
		sudo apt-get install -y nodejs) :<br>
	   To install mapshaper :<br>
		git clone https://github.com/mbloch/mapshaper<br>
		cd mapshaper<br>
		npm install
	</li>
	</ul>
</li>
<li>
3. Modify the script entry (at the top of the file) to match your own configuration :
	a. The three paths (to your tomcat, mapshaper and the directory to store files)
	b. The database informations (host, user, password and database name)
</li>
</ul>
