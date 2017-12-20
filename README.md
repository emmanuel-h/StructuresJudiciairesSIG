# StructuresJudiciairesSIG

<h1>To deploy the server</h1>
1. Create a directory to put the script coolScript and the FWTools installation from 2.b. It will also serve to store temporary files.
2. Install following the librairies :
	a. PyGreSQL :
		python-pygresql, python-pygresql-dbg (via synaptic or apt-get)
	b. FWTools (the jar is in the project root directory :
		tar xzvf FWTools-linux-2.0.6.tar.gz
		cd FWTools-linux-2.0.6
		./install.sh
	c. mapshaper. You must have Node.js on your computer, and not the old version in ubuntu packages. You can install the latest version with :
		curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash -
		sudo apt-get install -y nodejs) :
	   To install mapshaper :
		git clone https://github.com/mbloch/mapshaper
		cd mapshaper
		npm install
3. Modify the scripts entries (at the top of the file) to match your own configuration :
	a. coolScript : at the top of the file, the three paths
	b. postgis2shp.py : at the end of the file, the export_path and the database informations


If the python script doesn't know the _pg import, try install PygreSQL via pip : pip install PyGreSQL
