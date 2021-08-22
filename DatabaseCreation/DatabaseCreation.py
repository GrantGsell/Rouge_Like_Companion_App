import requests                     # Allows the use of get/put requests
from bs4 import BeautifulSoup       # Allows HTML data to be converted into a parsable object
from mysql.connector import MySQLConnection, Error
from python_mysql_dbconfig import read_db_config
from bs4 import NavigableString
import re
import cv2
from os.path import basename
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
from PIL import Image

class DatabaseCreation:

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def top_database_creation(self):
        try:
            # Clear and create tables
            self.clear_and_initialize_tables()

            # Create the object table and quality table
            self.write_objects_to_objects_table()
            self.write_qualities_to_sql()

            # Get the names of each weapon
            weapon_names = self.get_gun_names()
            item_names = self.get_item_names()

            # Iterate over the names list and add data to database
            for name in weapon_names:
                if name != 'Gunderfury' and name != 'Chamber_Gun':
                    # Obtain data
                    data_object = GungeonWeaponObject(name)
                    data_object.new_weapon(data_object.name, weapon_names, item_names)

                else:
                    data_object = self.issueObjects(name)

                # Obtain object id from objects table
                name = name.replace("\'", "''")
                object_id = self.obtain_id_from_objects_table(name)

                # Write gun data to gun_stats table in the MySQL database
                self.write_gun_data_to_gun_stats_table(object_id, data_object)

                # Write the gun synergy data to the synergies table in the MySQL database
                self.write_synergy_information_to_synergies_table(object_id, data_object)

            # Iterate over the names list and add data to database
            item_stats_dictionary = self.get_item_stats()
            for name in item_names:
                # Obtain object id from objects table
                sql_name = name.replace("\'", "''")
                object_id = self.obtain_id_from_objects_table(sql_name)

                # Obtain item object data
                item_data_tuple = item_stats_dictionary.get(name)

                # Write object data to MySQL database
                self.write_item_data_to_item_stats_table(object_id, item_data_tuple)

                # Write the item synergy data to the synergies table in the MySQL database
                synergy_names_list = self.get_item_synergies(name, weapon_names, item_names)
                if synergy_names_list != None:
                    self.write_item_synergy_information_to_synergies_table(object_id, synergy_names_list)
        except:
            temp = 5
        return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    @staticmethod
    def get_gun_names():
        # Base URL
        url = 'https://enterthegungeon.gamepedia.com/Guns'

        # Obtain HTML page data, and check the get request was successful
        page = requests.get(url)
        if page.status_code == 200:
            print('HTML Get Success')
        else:
            print('An error has occurred: ' + str(page.status_code))
            return

        # Create a BeautifulSoup object out of the entire webpage
        soup = BeautifulSoup(page.content, 'html.parser')

        # Find and extract all gun names
        table_of_guns = soup.find('table').select('tbody > tr')
        gun_names = []
        for i in range(1, len(table_of_guns)):
            temp = table_of_guns[i].find_all(title=True)[0].get('title')
            gun_names.append(temp.replace(' ', '_'))

        # Return the list of gun names
        return gun_names

    """
    Name        :
    Purpose     :
    Parameters  : None
    Return      : A list of strings, denoting the name of each gun
    Notes       : In the first for loop, when i == 225 this item is skipped because it is simply a
                    second entry for the same gun at i == 224 that contains no 'a' element and no relevent information
                    The item that has the dual entry is the item 'Ruby Bracelet'
    """
    @staticmethod
    def get_item_names():
        # Base URL
        url = 'https://enterthegungeon.gamepedia.com/Items'

        # Obtain HTML page data, and check the get request was successful
        page = requests.get(url)
        if page.status_code == 200:
            print('HTML Get Success')
        else:
            print('An error has occurred: ' + str(page.status_code))
            return

        # Create a BeautifulSoup object out of the entire webpage
        soup = BeautifulSoup(page.content, 'html.parser')

        # Find and extract all gun names
        table_of_items = (soup.find('table').tbody.find_all('tr'))
        item_names = []
        for i in range(1, len(table_of_items)):
            if i == 28:
                item_names.append('IBomb_Companion_App')
            elif i != 225:
                item_names.append((table_of_items[i].select('td > a')[1]).text.replace(' ', '_'))

        # Return the list of gun names
        return item_names

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    #@staticmethod
    def get_gun_images(self, soup) -> list:
        try:
            # Find and extract the item's information
            obj_page = soup.find('table', class_='infoboxtable')
            gun_pic_url = obj_page.select('tbody > tr')[1].find(src=True).attrs.get("src")
            gun_pic = requests.get(gun_pic_url).content
            try:
                gun_quality = obj_page.select('tbody > tr')[4].find(src=True).attrs.get("alt")[0]
            except:
                gun_quality = soup.find('a', title='Quality').next.get("alt")[0]

        except IndexError:
            obj_page = soup.find('table', class_='infoboxtable')
            gun_pic = obj_page.select('tbody > tr')[1].find(src=True)
            gun_quality = obj_page.select('tbody > tr')[4].find(src=True)
            #print("Error")

        except requests.exceptions.InvalidSchema:
            obj_page = soup.find('table', class_='infoboxtable')
            gun_pic_url = obj_page.select('tbody > tr')[1].find(href=True).get("href")
            gun_pic = requests.get(gun_pic_url).content
            gun_quality = obj_page.select('tbody > tr')[4].find(src=True).get("alt")[0]

        # Return the full text to be processed
        return [gun_pic, gun_quality]

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def get_gun_info(self, item_name: str):
        # Base URL
        url = 'https://enterthegungeon.gamepedia.com/' + item_name

        # Obtain HTML page data, and check the get request was successful
        try:
            page = requests.get(url)
            soup = BeautifulSoup(page.content, 'html.parser')
            gun_pic, quality = self.get_gun_images(soup)

            # Obtain the weapon stats
            if item_name != 'Winchester':
                gun_stats = soup.find('div', class_='mw-parser-output').table.tbody.find_all('tr')
            else:
                gun_stats = soup.find('div', class_='mw-parser-output').find_all('table')[1].tbody.find_all('tr')

            # Obtain the objects that synergize with the current weapon
            synergy_info = None
            if soup.find('div', class_='mw-parser-output').select('ul')[0].parent.get("id", None) != "toc":
                synergy_info = soup.find('div', class_='mw-parser-output').select('ul')[0].find_all("li")
            else:
                synergy_info = soup.find('div', class_='mw-parser-output').select('ul')[1].find_all("li")

            # Pre-process synergy_info
            i = 0
            while i < len(synergy_info):
                if isinstance(synergy_info[i].next, NavigableString):
                    del synergy_info[i]
                    i -= 1
                i += 1

            # Refactor quality if no quality
            if quality == "N":
                quality = "N/A"

            return [gun_stats, gun_pic, quality, synergy_info]
        except AttributeError:
            print('Url ERROR')
            return

    """
    Name        : 
    Purpose     :
    Parameters  :
    Return      :
    """
    def write_objects_to_objects_table(self):
        # Obtain the names of all objects
        gun_list = self.get_gun_names()
        item_list = self.get_item_names()
        id_num = 1

        # Create full list of data tuples
        data = []
        for name in gun_list:
            tup = (id_num, name, True)
            data.append(tup)
            id_num += 1
        for name in item_list:
            tup = (id_num, name, False)
            data.append(tup)
            id_num += 1

        """
        Add the Objects to the mySQL database
        """
        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        # Step 2. Prepare the query statement
        query = "INSERT IGNORE INTO objects(object_id, object_name, is_gun) " \
                "VALUES(%s, %s, %s)"

        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 5. Execute the query
            cursor.executemany(query, data)

            # Step 6. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()
        return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def obtain_id_from_objects_table(self, name: str) -> int:
        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        # Step 2. Prepare the query statement
        name_with_quotes = "'" + name + "'"
        query = "SELECT object_id FROM objects WHERE object_name = " + name_with_quotes

        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 5. Execute the query
            cursor.execute(query)

            # Step 6. Get the data from the query
            if name == 'Master_Round':
                object_id = cursor.fetchall()[0][0]
            else:
                object_id = cursor.fetchone()[0]

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()
        return object_id

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def write_gun_data_to_gun_stats_table(self, object_id, object_data):
        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        # Step 2. Prepare the query statement
        query = "INSERT INTO gun_stats(gun_id, gun_name, damage, dps, fire_rate, gun_force, mag_size, max_ammo, " \
                "pic, quality, gun_range, reload_time, sell_price, shot_speed, speed, gun_type)" \
                "VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
        data = (object_id, object_data.name, object_data.damage, object_data.dps, object_data.fireRate,
                object_data.force, object_data.magSize, object_data.maxAmmo, object_data.pic,
                object_data.quality, object_data.range, object_data.reloadTime, object_data.sell,
                object_data.shotSpeed, object_data.speed, object_data.type)


        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 5. Execute the query
            cursor.execute(query, data)

            # Step 6. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()
        return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def write_synergy_information_to_synergies_table(self, object_id, object_data):
        query = "INSERT INTO synergies(object_id, synergy_object_name, synergy_object_text, synergy_is_gun)" \
                " VALUES(%s, %s, %s, %s)"
        data = []
        for syn in object_data.synergy:
            if syn.transformName is not None:
                for name in syn.synergy_object_names:
                    if len(name) > 2:
                        if(name == "Master_Round"):
                            syn_is_gun = False
                        else:
                            syn_is_gun = self.object_is_gun(name)
                        data.append((object_id, name, syn.synergy_text, syn_is_gun))
        if len(data) < 1:
            return

        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 5. Execute the query
            cursor.executemany(query, data)

            # Step 6. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()

        return

    """
        Name        :
        Purpose     :
        Parameters  :
        Return      :
        """

    def write_item_synergy_information_to_synergies_table(self, object_id, synergy_name_list):
        query = "INSERT INTO synergies(object_id, synergy_object_name, synergy_object_text, synergy_is_gun)" \
                " VALUES(%s, %s, %s, %s)"
        data = []
        for name in synergy_name_list:
            if (name == "Master_Round"):
                syn_is_gun = False
            else:
                syn_is_gun = self.object_is_gun(name)
            data.append((object_id, name, "", syn_is_gun))

        if len(data) < 1:
            return

        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 5. Execute the query
            cursor.executemany(query, data)

            # Step 6. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()

        return


    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    #@staticmethod
    def get_item_stats(self):
        # Base URL
        url = 'https://enterthegungeon.gamepedia.com/Items'

        # Obtain HTML page data, and check the get request was successful
        page = requests.get(url)
        if page.status_code == 200:
            print('HTML Get Success')
        else:
            print('An error has occurred: ' + str(page.status_code))
            return

        # Create a BeautifulSoup object out of the entire webpage
        soup = BeautifulSoup(page.content, 'html.parser')

        # Find and extract all gun names
        table_of_items = soup.find("table").select('tbody > tr')
        item_stats_dict = dict()
        for index in range(1, len(table_of_items)):
            if index != 225:
                # Obtain the HTML code for one item
                curr_item = table_of_items[index].find_all("td")

                # Obtain the item name
                item_name = curr_item[1].find("a").get("title")
                item_name = item_name.replace(" ", "_")
                item_name = item_name.replace("'", "\'")

                # Find the item image url, type and type
                try:
                    item_img_url = curr_item[0].find("img").get('src')
                    item_pic = requests.get(item_img_url).content
                except:
                    item_img_url = curr_item[0].next.get("href")
                    item_pic = requests.get(item_img_url).content

                item_type = curr_item[2].text.replace("\n", "")
                item_effect = curr_item[5].text.replace("\n", "")

                # Find the items quality(s) if applicable
                item_quality = "N/A"
                if curr_item[4].find("a").get("title") != "N/A":
                    item_quality_list = curr_item[4].find_all("img")
                    if len(item_quality_list) > 1:
                        temp_item_quality_string = ""
                        for elem in item_quality_list:
                            temp_item_quality_string += elem.get('alt')[0]
                        item_quality = temp_item_quality_string
                    else:
                        item_quality = item_quality_list[0].get('alt')[0]
                        item_quality_url = item_quality_list[0].get('src')

                # Append data found to item stats list
                item_stats_dict[item_name] = (item_pic, item_type, item_quality, item_effect)

            else:
                # Obtain the full Ruby_Bracelet effect text
                ruby_bracelet_full_effect = table_of_items[index].find_all("td")[1].text

                # Change effect in the item stats dictionary
                dict_tuple = item_stats_dict.get("Ruby_Bracelet")
                change_dict_tuple = list(dict_tuple)
                change_dict_tuple[3] = ruby_bracelet_full_effect
                item_stats_dict["Ruby_Bracelet"] = tuple(change_dict_tuple)

        # Return the list of gun names
        return item_stats_dict


    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def write_item_data_to_item_stats_table(self, object_id, item_data):
        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        # Step 2. Prepare the query statement
        query = "INSERT INTO item_stats(object_id, img, item_type, quality, effect) " \
                "VALUES(%s, %s, %s, %s, %s)"

        item_data = list(item_data)
        item_data.insert(0, object_id)
        data = item_data

        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 5. Execute the query
            cursor.execute(query, item_data)

            # Step 6. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()
        return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def write_qualities_to_sql(self):
        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        # Step 2. Prepare the query statement
        query = "INSERT INTO object_quality (quality_letter, quality_image) " \
                "VALUES(%s, %s)"

        quality_letter_arr = ["1", "A", "B", "C", "CBA", "D", "N/A"]
        quality_imgs = ["1S_Quality_Item.png", "A_Quality_Item.png", "B_Quality_Item.png", "C_Quality_Item.png",
                        "CBA.png", "D_Quality_Item.png", "N_Quality_Item.png"]

        try:
            # Step 3. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 4. Create a new MySQLCursor object
            cursor = conn.cursor()

            for i in range(len(quality_imgs)):
                img_dat = self.convertToBinaryData(quality_imgs[i])
                data = (quality_letter_arr[i], img_dat)

                # Step 5. Execute the query
                cursor.execute(query, data)

            # Step 6. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 7. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()
        return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def convertToBinaryData(self, filename):
        # Convert digital data to binary format
        with open(filename, 'rb') as file:
            binaryData = file.read()
        return binaryData

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def object_is_gun(self, object_name) -> bool:
        is_gun = False

        # Remove any unnecessary slashes
        object_name_quotes = object_name.replace("\'", "\\'")

        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        try:
            # Step 2. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 3. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Create and execute query string
            query = "SELECT is_gun " \
                    "FROM objects " \
                    "WHERE object_name = " + "'" + object_name_quotes + "'"
            cursor.execute(query)

            # Extract data
            is_gun = cursor.fetchone()
            if object_name == "Master_Round":
                is_gun = False
            else:
                is_gun = is_gun[0]

        except Error as error:
            print(error)

        # Step 6. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()

        return is_gun

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def clear_and_initialize_tables(self):
        # Step 1. Read in the database configuration file data
        db_config = read_db_config()

        try:
            # Step 2. Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Step 3. Create a new MySQLCursor object
            cursor = conn.cursor()

            # Step 4. Create query strings and execute them
            # Clear the tables
            query = "DROP TABLE IF EXISTS object_quality;"
            cursor.execute(query)
            query = "DROP TABLE IF EXISTS gun_stats;"
            cursor.execute(query)
            query = "DROP TABLE IF EXISTS synergies;"
            cursor.execute(query)
            query = "DROP TABLE IF EXISTS item_stats;"
            cursor.execute(query)
            query = "DROP TABLE IF EXISTS objects;"
            cursor.execute(query)

            # Initialize each table
            query = "CREATE TABLE IF NOT EXISTS objects ( " \
                    "object_id int auto_increment primary key, " \
                    "object_name varchar(255) not null, " \
                    "is_gun boolean not null );"
            cursor.execute(query)
            query = "CREATE TABLE IF NOT EXISTS gun_stats ( " \
                    "gun_id int not null, " \
                    "gun_name varchar(255) not null, " \
                    "damage varchar(255), " \
                    "dps varchar(255), " \
                    "fire_rate varchar(255), " \
                    "gun_force varchar(255), " \
                    "mag_size varchar(255), " \
                    "max_ammo varchar(255), " \
                    "pic blob, " \
                    "quality varchar(3), " \
                    "gun_range varchar(255), " \
                    "reload_time varchar(255), " \
                    "sell_price varchar(255), " \
                    "shot_speed varchar(255), " \
                    "speed varchar(255), " \
                    "gun_type varchar(255), " \
                    "foreign key(gun_id) references objects(object_id) );"
            cursor.execute(query)
            query = "CREATE TABLE IF NOT EXISTS synergies ( " \
                    "object_id int, " \
                    "synergy_object_name varchar(255), " \
                    "synergy_object_text text, " \
                    "synergy_is_gun boolean not null, " \
                    "foreign key(object_id) references objects(object_id) );"
            cursor.execute(query)
            query = "CREATE TABLE IF NOT EXISTS item_stats ( " \
                    "object_id int, " \
                    "item_type varchar(7), " \
                    "quality varchar(3), " \
                    "effect text, " \
                    "img blob, " \
                    "foreign key(object_id) references objects(object_id) );"
            cursor.execute(query)
            query = "CREATE TABLE IF NOT EXISTS object_quality ( " \
                    "quality_letter varchar(3) PRIMARY KEY, " \
                    "quality_image BLOB );"
            cursor.execute(query)

            # Step 5. Accept the changes to ensure the data is updated in teh table
            conn.commit()

        except Error as error:
            print(error)

        # Step 6. Close the cursor object nd close the database connection object
        finally:
            cursor.close()
            conn.close()
        return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def issueObjects(self, object_name):
        # Create a new Weapon object
        newObj = GungeonWeaponObject(object_name)

        # Set similar values
        newObj.magSize = "Variable"
        newObj.maxAmmo = "Variable"
        newObj.dps = "Variable"
        newObj.damage = "Variable"
        newObj.reloadTime = "Variable"
        newObj.fireRate = "Variable"
        newObj.shotSpeed = "Variable"
        newObj.range = "Variable"
        newObj.force = "Variable"
        newObj.speed = "Variable"

        # Set issue weapon stats based on name
        if(object_name == "Gunderfury"):
            newObj.type = "Variable"
            newObj.quality = "A"
            newObj.sell = "41"
            newObj.unlock = "Visit all chambers"
            newObj.pic = requests.get("https://static.wikia.nocookie.net/enterthegungeon_gamepedia/images/a/a7/"
                                    "Gunderfury.png/revision/latest/scale-to-width-down/80?cb=20190406104933").content

            # Add synergy objects
            synObj0 = SynergyObj("Chance_On_Hit", ["Silver_Bullets"], "Obtain a chance to slow enemies and make "
                                                                    "Gunderfury's bullets act like they have Shock "
                                                                    "Rounds")
            synObj1 = SynergyObj("Worlds_Of_Guncraft", ["Mr._Accretion_Jr."], "Occasionally shoot out Mr. Accretion Jr. "
                                                                            "shots.")
            newObj.synergy = [synObj0, synObj1]
        else:
            newObj.type = "Semiautomatic"
            newObj.quality = "C"
            newObj.sell = "21"
            newObj.unlock = "Purchase from Doug"
            newObj.pic = requests.get("https://static.wikia.nocookie.net/enterthegungeon_gamepedia/images/1/16/"
                                    "Chamber_Gun.png/revision/latest/scale-to-width-down/75?cb=20190406195820").content

            # Add synergy objects
            synObj = SynergyObj("Master\'s_Chambers", ["Master_Round"], "Each Master Round the player carries permanently"
                                                                      " unlocks the mode of its respective floor.")
            newObj.synergy = [synObj]

        return newObj


    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def get_item_synergies(self, item_name, weapon_names, item_names):
        # Base URL
        url = 'https://enterthegungeon.gamepedia.com/' + item_name

        # Obtain HTML page data, and check the get request was successful
        try:
            page = requests.get(url)
            soup = BeautifulSoup(page.content, 'html.parser')

            # Obtain all tags that start with a marker
            try:
                synergy_info = soup.find('div', class_='mw-parser-output').find(alt="Synergy.png").parent.parent.parent.find_all("li")
            except:
                return

            # Add any tags whose title appears in the items or weapons lists
            synergizes_with_names = []
            for tag in synergy_info:
                titles = tag.find_all('a', title=True)
                for titleTag in titles:
                    title = titleTag.get("title")
                    parentTitleTag = titleTag.parent.find(title=True).get("title")
                    if (title.replace(' ', '_') in item_names or title.replace(' ', '_') in weapon_names) and parentTitleTag[0:10] == "Synergies#":
                        synergizes_with_names.append(title.replace(' ', '_'))

            return synergizes_with_names
        except AttributeError:
            print('Url ERROR')
            return

    """
    Name        :
    Purpose     :
    Parameters  :
    Return      :
    """
    def create_and_populate_shrine_table(self):
        # Base URL
        url = "https://enterthegungeon.fandom.com/wiki/Shrines"

        # Obtain HTML page data, and check the get request was successful
        try:
            page = requests.get(url)
            soup = BeautifulSoup(page.content, 'html.parser')
        except:
            return

        # Find and extract rwo entries for each shrine
        table_of_shrines = soup.find('table', class_="wikitable").select("tbody")
        temp = soup.find('table', class_="wikitable").select("tbody > tr")

        # Extract each shrine name image and effect
        results = []
        dice_shrine = []
        dice_shrine_good = []
        dice_shrine_bad = []
        for i in range(1, len(temp)):
            individual_shrine_data = temp[i]

            if i > 8 and i < 21:
                if i == 9:
                    shrine_image_url = individual_shrine_data.contents[1].next.get("href")
                    shrine_image = requests.get(shrine_image_url).content
                    shrine_name = individual_shrine_data.contents[1].text
                    dice_shrine.append([shrine_name, shrine_image, ""])
                    continue
                elif i == 10: continue
                elif i == 20:
                    (dice_shrine[0])[2] = individual_shrine_data.contents[1].text
                    results.append(dice_shrine[0])
                    continue

                good_effect_name = individual_shrine_data.contents[1].text.replace("\n", "")
                good_effect_text = individual_shrine_data.contents[3].text.replace("\n", "")
                bad_effect_name = individual_shrine_data.contents[5].text.replace("\n", "")
                bad_effect_text = individual_shrine_data.contents[7].text.replace("\n", "")
                dice_shrine_good.append([good_effect_name, good_effect_text])
                dice_shrine_bad.append([bad_effect_name, bad_effect_text])
                continue

            else:
                shrine_effect = individual_shrine_data.contents[7].text
                shrine_image_url = individual_shrine_data.contents[1].next.get("href")
                shrine_image = requests.get(shrine_image_url).content
                shrine_name = individual_shrine_data.contents[1].text.replace("\n", "")

            results.append([shrine_name, shrine_image, shrine_effect])

        # Read in the database configuration file data
        db_config = read_db_config()

        # Create tables for shrines
        try:
            # Connect to the MySQL database by creating a new MySQLConnection object
            conn = MySQLConnection(**db_config)

            # Create a new MySQLCursor object
            cursor = conn.cursor()

            # Clear the tables
            query = "DROP TABLE IF EXISTS shrines;"
            cursor.execute(query)
            query = "DROP TABLE IF EXISTS good_dice_shrine_effects;"
            cursor.execute(query)
            query = "DROP TABLE IF EXISTS bad_dice_shrine_effects;"
            cursor.execute(query)

            # Initialize each table
            query = "CREATE TABLE IF NOT EXISTS shrines ( " \
                    "shrine_name text, " \
                    "shrine_image blob, " \
                    "shrine_effect text );"
            cursor.execute(query)
            query = "CREATE TABLE IF NOT EXISTS good_dice_shrine_effects ( " \
                    "good_name text," \
                    "effect text );"
            cursor.execute(query)
            query = "CREATE TABLE IF NOT EXISTS bad_dice_shrine_effects ( " \
                    "bad_name text," \
                    "effect text );"
            cursor.execute(query)

            # Populate shrine table
            query = "INSERT INTO shrines(shrine_name, shrine_image, shrine_effect) " \
                    "VALUES(%s, %s, %s)"
            cursor.executemany(query, results)
            conn.commit()

            # Populate good dice shrine effects
            query = "INSERT INTO good_dice_shrine_effects(good_name, effect) " \
                    "VALUES(%s, %s)"
            cursor.executemany(query, dice_shrine_good)
            conn.commit()

            # Populate bad dice shrine effects
            query = "INSERT INTO bad_dice_shrine_effects(bad_name, effect) " \
                    "VALUES(%s, %s)"
            cursor.executemany(query, dice_shrine_bad)
            conn.commit()


        except Exception as e:
            print(e)


class SynergyObj:
    def __init__(self, transform_name=None, synergy_object_names=None, synergy_text=None):
        self.transformName = transform_name
        self.synergy_object_names = synergy_object_names
        self.synergy_text = synergy_text


class GungeonWeaponObject:
    def __init__(self, name=None):
        self.name = name
        self.type = "N/A"
        self.quality = "N/A"
        self.magSize = "N/A"
        self.maxAmmo = "N/A"
        self.reloadTime = "N/A"
        self.dps = "N/A"
        self.damage = "N/A"
        self.fireRate = "N/A"
        self.shotSpeed = "N/A"
        self.range = "N/A"
        self.force = "N/A"
        self.speed = "N/A"
        self.sell = "N/A"
        self.unlock = "N/A"
        self.pic = "N/A"
        self.synergy = None

    def new_weapon(self, name: str, weapon_names, item_names):
        validDataId = {
            "Type"             : 0,
            "Quality"          : 1,
            "Magazine Size"    : 2,
            "Max Ammo"         : 3,
            "Reload Time"      : 4,
            "DPS"              : 5,
            "Damage"           : 6,
            "Fire Rate"        : 7,
            "Shot Speed"       : 8,
            "Range"            : 9,
            "Force"            : 10,
            "Spread"           : 11,
            "Sell Creep Price" : 12,
            "Unlock"           : 13
        }

        # Get the table info in the form of a string array representation for the given gun
        gun_stats, self.pic, quality, synergy_info = DatabaseCreation().get_gun_info(name)

        # Instantiate containers for string data
        gun_info = [None] * len(gun_stats)
        gun_syn = []
        syn_text = []

        # Synergy information preprocessing
        for j in range(len(synergy_info)):
            syn_sub_list = synergy_info[j].find_all('a', title=True)
            if(len(syn_sub_list) > 0 and syn_sub_list[0].get("title", None) is not None and
                syn_sub_list[0].get("title", None)[0:10] == "Synergies#"):
                syn_text.append(synergy_info[j].text)
                gun_syn.append(syn_sub_list)

        # Extract the transformation name first, then all object names that cause the synergy
        # Ensure all names after the first are either a weapon or item
        gun_names_per_syn = []
        for k in range(len(gun_syn)):
            temp_arr = []
            all_curr_titles = gun_syn[k]
            for m in range(len(all_curr_titles)):
                curr_title = all_curr_titles[m].get("title").replace("Synergies#", "").replace("Synergies", "").replace(" ", "_")
                if curr_title != "":
                    if m == 0 or (curr_title in weapon_names) or (curr_title in item_names):
                        temp_arr.append(curr_title)

            gun_names_per_syn.append(temp_arr)

        # Add the synergy names to the synergy object
        synergy_objects = []
        for idx in range(len(syn_text)):
            new_syn_obj = SynergyObj()
            syn_text[idx] = self.syn_text_truncate(syn_text[idx], gun_names_per_syn[idx])
            new_syn_obj.synergy_text = syn_text[idx]
            if len(gun_names_per_syn[idx]) > 1:
                new_syn_obj.transformName = gun_names_per_syn[idx][0]
                new_syn_obj.synergy_object_names = gun_names_per_syn[idx][1:]
            else:
                new_syn_obj.synergy_object_names = gun_names_per_syn[idx]
            synergy_objects.append(new_syn_obj)
        self.synergy = synergy_objects

        i = 0
        # Gun stat preprocessing
        for info in gun_stats:
            gun_info[i] = info.text.replace('\n', '')
            i += 1

        # Iterate over gun info and add appropriate info
        for i in range(3, len(gun_info)):
            info = gun_info[i]

            # Find the index of the first instance of a ':' dont assume it exists
            colon_idx = info.find(':')

            # Search for the string from 0 -> the index just found within the validDataId Hashtable
            # If True then assign the info based on a cumbersome if/else block
            if colon_idx != -1 and validDataId.get(info[:colon_idx], None) is not None:
                idx = validDataId.get(info[:colon_idx])
                if idx == 0: self.type = info[colon_idx+1:]
                elif idx == 1: self.quality = quality
                elif idx == 2: self.magSize = info[colon_idx+1:]
                elif idx == 3: self.maxAmmo = info[colon_idx+1:]
                elif idx == 4: self.reloadTime = info[colon_idx+1:]
                elif idx == 5: self.dps = info[colon_idx+1:]
                elif idx == 6: self.damage = info[colon_idx+1:]
                elif idx == 7: self.fireRate = info[colon_idx+1:]
                elif idx == 8: self.shotSpeed = info[colon_idx+1:]
                elif idx == 9: self.range = info[colon_idx+1:]
                elif idx == 10: self.force = info[colon_idx+1:]
                elif idx == 11: self.speed = info[colon_idx+1:]
                elif idx == 12: self.sell = info[colon_idx+1:]
                elif idx == 13: self.unlock = info[colon_idx+1:]

        return


    def syn_text_truncate(self, syn_text, gun_names_per_syn):
        try:
            # Remove all string data before last synergy name
            start_last_syn_name = str.rindex(syn_text, gun_names_per_syn[-1].replace("_", " "), 0, len(syn_text))
            last_index = start_last_syn_name + len(gun_names_per_syn[-1]) + 2
            syn_text_temp = syn_text[last_index: len(syn_text)]

            # Split the text at the first period and obtain only the first sentence
            syn_text_temp = syn_text_temp.split(".")[0]

            return syn_text_temp

        except (IndexError, ValueError) as e:
            print("Synergy Text Truncate Method error")
            syn_text_temp = syn_text.split("-")[1]
            syn_text_temp = syn_text_temp[1: len(syn_text_temp)]
            return syn_text_temp

def main():
    DatabaseCreation().create_and_populate_shrine_table()

    DatabaseCreation().top_database_creation()

    # Get the names of each weapon
    #weapon_names = DatabaseCreation().get_gun_names()
    #item_names = DatabaseCreation().get_item_names()
    #GungeonWeaponObject().new_weapon("Winchester", weapon_names, item_names)
    #GungeonWeaponObject().new_weapon("Chamber_Gun", weapon_names, item_names)



if __name__ == "__main__":
    main()
