#### Author: Justin Lewis
#### This script can be used to export a single pg table to shapefile.
####
#### Developed in a linux environment but should require minimal modification to run on other platforms.
####
#### Dependancies:
###### fwtools (gdal > ogr2ogr), PostGIS database, Python, PyGreSQL

import os, _pg

class pg_toshape:

    def pg_table_names(self, db):
        tableList = []

        print "These are the tables in the 'public' schema of your database. Use them for reference for the next step."
        print ""
        sel = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' and table_catalog = '{db}';".format(db = db)
        sel = conn.query(sel)
        result = sel.getresult()
        for t in result:
            t = self.strip_list_entries(t)
            print t
        print ""
        print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        print ""

        table = raw_input("Enter the name of the table you want to export: ")
        if table != '':
            tableList.append(table)
        more = 'y'
        while more == 'y':
            more = raw_input("Any more? ('y' or 'n') ")
            if more == 'y':
                table = raw_input("Enter the name of the table: ")
                if table in tableList:
                    print table, " is already in your list of tables."
                else:
                    tableList.append(table)
            elif more == 'n':
                print "These are the tables you selected to export:"
                for t in tableList:
                    print t
        return tableList

    def export_pg_table(self, export_path, pgtable_name, host, username, password, db, pg_sql_select):

        print "Exporting shapefile using the ", geom, "geometry field..."
        cmd = '''ogr2ogr -overwrite -f \"ESRI Shapefile\" {export_path}{pgtable_name}.shp PG:"host={host} user={username} dbname={db} password={password}" -sql "{pg_sql_select}"'''.format(pgtable_name = pgtable_name, export_path = export_path, host = host, username = username, db = db, password = password, pg_sql_select = pg_sql_select)
        os.system(cmd)

    def strip_list_entries(self, entry):

        entry = str(entry)
        entry = entry.lstrip("('")
        entry = entry.rstrip("',)")
        return entry

    def select_geom(self, pgtable_name, host, username, password, db):

        sel = "select f_geometry_column from geometry_columns where f_table_name = '{pgtable_name}';".format(pgtable_name = pgtable_name)
        sel = conn.query(sel)
        result = sel.getresult()
        ct = len(result)
        if ct > 1:
            geomList = []
            print "There are ", ct, "geometry fields in {pgtable_name}.".format(pgtable_name = pgtable_name)
            print "-------------------------"
            for f in result:
                f = self.strip_list_entries(f)
                print f
                geomList.append(f)
            print "-------------------------"
            geom = raw_input("Enter the geom field to export: ")
            print "-------------------------"
            while geom not in geomList:
                print "-------------------------"
                print "Incorect field name. Try again."
                geom = raw_input("Enter the geom field to export: ")
            return geom
        else:
            for f in result:
                geom = self.strip_list_entries(f)
                return geom

    def select_field_names(self, pgtable_name, geom):
        sel = "select column_name from information_schema.columns where table_name = '{pgtable_name}' AND udt_name <> 'geometry';".format(pgtable_name = pgtable_name)
        sel = conn.query(sel)
        result = sel.getresult()
        fields = geom
        for f in result:
            f = self.strip_list_entries(f)
            fields = f + ", " + fields
        return fields

    def select_srid(self, pgtable_name, host, username, password, db, geom):

        sel = "SELECT ST_SRID({geom}) AS srid FROM {pgtable_name} GROUP BY srid;".format(pgtable_name = pgtable_name, geom = geom)
        sel = conn.query(sel)
        result = sel.getresult()
        for s in result:
            srid = self.strip_list_entries(s)
        print ""
        print "The source table is stored with srid = ", srid
        return srid

    def set_srid(self, srid):

        print "Generating a 2nd shapefile with the correct SRID and calling it {pgtable_name}_{srid}.shp...".format(pgtable_name = pgtable_name, srid = srid)
        cmd = "ogr2ogr -overwrite -a_srs EPSG:{srid} {export_path}{pgtable_name}_{srid}.shp {export_path}{pgtable_name}.shp".format(pgtable_name = pgtable_name, export_path = export_path, srid = srid)
        process = os.system(cmd)

    def transform_srid(self, source_srid, pgtable_name, export_path):

        print "-------------------------"
        print "Current SRID = ", source_srid
        print "-------------------------"
        answer = ['y', 'n']
        set_new_srid = raw_input("Would you like to set a different SRID (y or n)? ")
        while set_new_srid not in answer:
            set_new_srid = raw_input("Would you like to set a different SRID (y or n)? ")
            if set_new_srid == 'y':
                new_srid = raw_input("What SRID would you like to transform the data to? ")
                cmd = "ogr2ogr -overwrite -s_srs EPSG:{source_srid} -t_srs EPSG:{new_srid} {export_path}{pgtable_name}_{new_srid}.shp {export_path}{pgtable_name}_{source_srid}.shp".format(pgtable_name = pgtable_name, source_srid = source_srid, new_srid = new_srid, export_path = export_path)
                process = os.system(cmd)
            else:
                pass




if __name__ == '__main__':

    #### Export paramaters
    export_path = "~/Documents/serverPython/"

    #### Connection paramaters for the PostGIS database.
    host = "localhost"
    username = "postgres"
    password = "postgres"
    db = "StructureJudiciaire"
    conn = _pg.connect(dbname = db, host= host, port= 5432, user = username, passwd= password)


    #### Running the script
    pg_toshape = pg_toshape()
    # No longer need - use to ask the user the tables he wants to export
    #table_list = pg_toshape.pg_table_names(db)
    table_list = ['annuaire_lieux_justice','annuaire_tgi','annuaire_ti','liste_des_greffes']


    for table in table_list:
        pgtable_name = table

        geom = pg_toshape.select_geom(pgtable_name, host, username, password, db)
        select_fields = pg_toshape.select_field_names(pgtable_name, geom)
        pg_sql_select = "SELECT " + select_fields + " FROM " + pgtable_name + ";"

        pg_toshape.export_pg_table(export_path, pgtable_name, host, username, password, db, pg_sql_select)
        source_srid = pg_toshape.select_srid(pgtable_name, host, username, password, db, geom)
        pg_toshape.set_srid(source_srid)
        # No longer need - use to change the default SRID
        #pg_toshape.transform_srid(source_srid, pgtable_name, export_path)


    print ""
    print "Process complete."
