# StructuresJudiciairesSIG

To deploy the server :
1. Install following the librairies :
	a. Pygresql :
		python-pygresql, python-pygresql-dbg (via synaptic or apt-get)
	b. FWTools (the jar is in the project root directory :
		tar xzvf FWTools-linux-2.0.6.tar.gz
		FWTools-linux-0.9.5
		./install.sh
	c. mapshaper (you must have Node.js on your computer)
		git clone git@github.com:mbloch/mapshaper.git
		cd mapshaper
		npm install
3. Create a directory to put the script coolScript. It will also serve to store temporary files.
4. Modify the script entries (at the top of the file) to match your own configuration 
