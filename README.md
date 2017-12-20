# StructuresJudiciairesSIG

<h2>To deploy the server</h2>
1. Create a directory to put the script coolScript and the FWTools installation from 2.b. It will also serve to store temporary files.
2. Install following the librairies :
	a. FWTools (the jar is in the project root directory :
		tar xzvf FWTools-linux-2.0.6.tar.gz
		cd FWTools-linux-2.0.6
		./install.sh
	b. mapshaper. You must have Node.js on your computer, and not the old version in ubuntu packages. You can install the latest version with :
		curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash -
		sudo apt-get install -y nodejs) :
	   To install mapshaper :
		git clone https://github.com/mbloch/mapshaper
		cd mapshaper
		npm install
3. Modify the script entry (at the top of the file) to match your own configuration :
	a. The three paths (to your tomcat, mapshaper and the directory to store files)
	b. The database informations (host, user, password and database name)
