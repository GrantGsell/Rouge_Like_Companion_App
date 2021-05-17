import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInterface {
    // Class fields
    public static List<JTextPane> weapon_list = new ArrayList<JTextPane>();
    public static List<JTextPane> item_list = new ArrayList<JTextPane>();
    public static List<JTextPane> synergy_list = new ArrayList<JTextPane>();
    public static JScrollPane all_weapons = new JScrollPane();
    public static JScrollPane all_items = new JScrollPane();
    public static JScrollPane all_synergies = new JScrollPane();
    public static JPanel main_panel = new JPanel(new BorderLayout());
    public static JFrame frame = new JFrame();
    public static JButton new_run_button = new JButton("New Run");
    public static ArrayList<String> processed_objects = new ArrayList<String>();

    public static void run_user_interface() {
        all_weapons = total_weapon_data_panel(weapon_list);
        all_items = total_item_data_panel(item_list);
        all_synergies = total_synergy_data_panel(synergy_list);

        JSplitPane split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, all_items, all_synergies);
        split_pane.setBorder(BorderFactory.createMatteBorder(0, 6, 0, 0, Color.BLACK));

        //JPanel main_panel = new JPanel(new BorderLayout());
        main_panel.add(all_weapons, BorderLayout.WEST);
        main_panel.add(split_pane, BorderLayout.CENTER);

        // Add a button to clear data for a new run
        main_panel.add(new_run_button, BorderLayout.PAGE_START);

        // Set Frame components
        frame.setTitle("Companion App");
        frame.setLayout(new BorderLayout());
        frame.add(main_panel, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);

        split_pane.setResizeWeight(0.5f);

        // Add button functionality
        new_run_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new_run();
            }
        });
    }



    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void update_frame(){
        //frame.setVisible(false);
        //frame.remove(main_panel);
        main_panel.setVisible(false);
        main_panel.removeAll();

        JSplitPane split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, all_items, all_synergies);
        split_pane.setBorder(BorderFactory.createMatteBorder(0, 6, 0, 0, Color.BLACK));

        //JPanel main_panel = new JPanel(new BorderLayout());
        main_panel.add(all_weapons, BorderLayout.WEST);
        main_panel.add(split_pane, BorderLayout.CENTER);
        split_pane.setResizeWeight(0.5f);

        // Add a button to clear data for a new run
        main_panel.add(new_run_button, BorderLayout.PAGE_START);

        main_panel.setVisible(true);
        //frame.add(main_panel);
        //frame.setVisible(true);
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void new_run(){
        // Clear object data lists
        weapon_list.clear();
        item_list.clear();
        synergy_list.clear();
        processed_objects.clear();

        // Clear the java frames
        all_weapons = total_weapon_data_panel(weapon_list);
        all_items = total_item_data_panel(item_list);
        all_synergies = total_synergy_data_panel(synergy_list);

        //add_weapon("\'Trident\'", weapon_list);
        //add_weapon("\'Casey\'", weapon_list);
        //add_item( "\'Ration\'", item_list);
        //add_item( "\'Orange\'", item_list);

        //all_weapons = total_weapon_data_panel(weapon_list);

        // Update the frame
        update_frame();
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JScrollPane add_weapon(String weapon_name, List<JTextPane> curr_data){
        // Create a new weapon text frame and add it to the current data list
        indiv_weapon_panel_data(weapon_name, curr_data);

        // Update weapons panel
        all_weapons = total_weapon_data_panel(curr_data);

        // Update the total weapon pane with the new weapon data
        return null;// total_weapon_data_panel(curr_data);
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JTextPane new_weapon_text_pane(String weapon_name) {
        // Create new JTextPane, initialize settings
        JTextPane new_weapon_pane = new JTextPane();
        new_weapon_pane.setPreferredSize(new Dimension(175, 300));
        Font font = new Font("Serif", Font.PLAIN, 18);
        new_weapon_pane.setFont(font);

        // Obtain the specified weapon stats
        List<Object> weapon_stats = MySQLAccess.obtain_gun_stats(weapon_name);

        // Add the weapon quality image to JTextPane
        int qsf = 2; // Quality scale factor
        BufferedImage quality_img = (BufferedImage) weapon_stats.get(6);
        Image scaled_quality_img = quality_img.getScaledInstance(quality_img.getWidth() * qsf, quality_img.getHeight() * qsf, BufferedImage.TYPE_INT_ARGB);
        new_weapon_pane.insertIcon(new ImageIcon(scaled_quality_img));

        // Add the weapon mage to JTextPane
        int sf = 3; // scale factor
        BufferedImage weapon_img = (BufferedImage) weapon_stats.get(5);
        Image scaled_weapon_img = weapon_img.getScaledInstance(weapon_img.getWidth() * sf, weapon_img.getHeight() * sf, BufferedImage.TYPE_INT_ARGB);
        new_weapon_pane.insertIcon(new ImageIcon(scaled_weapon_img));

        // Append text to JTextPane Document
        try {
            // Create string text
            String text = "\nName: " + weapon_stats.get(0) +
                    "\nDPS: " + weapon_stats.get(1) +
                    "\nReload Time: " + weapon_stats.get(2) +
                    "\nSell Price: " + weapon_stats.get(3) +
                    "\nGun Type: " + weapon_stats.get(4) + "\n";

            Document doc = new_weapon_pane.getDocument();
            doc.insertString(doc.getLength() / 2, text, null);
        }
        catch(BadLocationException ex){
            System.out.println(ex);
        }
        return new_weapon_pane;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static List<JTextPane> indiv_weapon_panel_data(String weapon_name, List<JTextPane> curr_data){
        // Create new JTextPane with the weapon info
        JTextPane info_text_pane = new_weapon_text_pane(weapon_name);
        info_text_pane.setBackground(new Color(241, 241, 241));
        info_text_pane.setEditable(false);
        if(curr_data.size() > 0) {
            LineBorder last_color = (LineBorder) curr_data.get(curr_data.size() - 1).getBorder();
            if(last_color.getLineColor().getRed() != 0){
                info_text_pane.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
            }
            else{
                info_text_pane.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            }
        }
        else{
            info_text_pane.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        }

        // Add JTextPane to list
        curr_data.add(info_text_pane);

        return curr_data;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JScrollPane total_weapon_data_panel(List<JTextPane> curr_data){
        JPanel total_weapon_pane = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        //infoPanel.setAutoscrolls(true);
        int cols = 5;
        int rows = (int) Math.ceil((double) curr_data.size() / (double) cols);
        total_weapon_pane.setLayout(new GridLayout(rows, cols));
        for(JTextPane data : curr_data){
            total_weapon_pane.add(data);
        }
        JScrollPane scroll = new JScrollPane(total_weapon_pane);
        scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Weapons", TitledBorder.CENTER, TitledBorder.TOP));
        scroll.setPreferredSize(new Dimension(900, 325 * 2));
        return scroll;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JScrollPane add_item(String item_name, List<JTextPane> curr_item_data){
        // Create a new weapon text frame and add it to the current data list
        new_item_text_pane(item_name, curr_item_data);

        // Update item JScroll Pane
        all_items = total_item_data_panel(curr_item_data);
        all_items.setAutoscrolls(true);

        // Update the total weapon pane with the new weapon data
        return null; //total_item_data_panel(curr_item_data);
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
    */
    public static List<JTextPane> new_item_text_pane(String item_name, List<JTextPane> curr_item_data){
        // Create new JTextPane, initialize settings
        JTextPane new_item_pane = new JTextPane();
        //new_item_pane.setPreferredSize(new Dimension(900, 60));
        Font font = new Font("Serif", Font.PLAIN, 16);
        new_item_pane.setFont(font);

        // Obtain the specified weapon stats
        List<Object> item_stats = MySQLAccess.obtain_item_stats(item_name);

        // Add the weapon mage to JTextPane
        int sf = 3; // scale factor
        BufferedImage item_img = (BufferedImage) item_stats.get(0);
        Image scaled_item_img = item_img.getScaledInstance(item_img.getWidth() * sf, item_img.getHeight() * sf, BufferedImage.TYPE_INT_ARGB);
        new_item_pane.insertIcon(new ImageIcon(scaled_item_img));
        if(item_img.getHeight() * sf + 20 > 80) {
            new_item_pane.setPreferredSize(new Dimension(900, item_img.getHeight() * sf + 20));
        }else{
            new_item_pane.setPreferredSize(new Dimension(900, 70));
        }

        // Append text to JTextPane Document
        try {
            // Create string text
            String text = "\tEffect: " + item_stats.get(1) + "\tItem Type: " + item_stats.get(2);

            StyledDocument doc = (StyledDocument ) new_item_pane.getDocument();
            doc.insertString(doc.getLength(), text, null);
            SimpleAttributeSet attrs= new SimpleAttributeSet();
            doc.setParagraphAttributes(0,doc.getLength()-1,attrs,false);

            // Try to obtain content height and change textpane height
            int num_chars = doc.getLength();
            if(num_chars > 175 && num_chars < 350){
                new_item_pane.setPreferredSize(new Dimension(900, 80));
            }else if(num_chars > 350){
                new_item_pane.setPreferredSize(new Dimension(900, 100));
            }
        }
        catch(BadLocationException ex){
            System.out.println(ex);
        }

        // Set border data
        new_item_pane.setBackground(new Color(241, 241, 241));
        new_item_pane.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));

        // Add new item to list
        curr_item_data.add(new_item_pane);

        return curr_item_data;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JScrollPane total_item_data_panel(List<JTextPane> curr_data){
        //JPanel total_item_pane = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        //infoPanel.setAutoscrolls(true);
        JPanel total_item_pane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int cols = 1;
        int rows = curr_data.size();
        //total_item_pane.setLayout();
        int idx = 0;
        for(JTextPane data : curr_data){
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = idx;
            total_item_pane.add(data, c);
            idx += 1;
        }
        JScrollPane scroll = new JScrollPane(total_item_pane);
        scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder (), "Items", TitledBorder.CENTER, TitledBorder.TOP));
        scroll.setPreferredSize(new Dimension(900, 50));
        return scroll;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JScrollPane add_synergy(String object_name, List<JTextPane> curr_syn_data){
        // Create a new weapon text frame and add it to the current data list
        new_synergy_text_pane(object_name, curr_syn_data);

        // Update synergy JScroll Pane
        all_synergies = total_synergy_data_panel(curr_syn_data);
        all_synergies.setAutoscrolls(true);

        // Update the total weapon pane with the new weapon data
        return null; // total_synergy_data_panel(curr_syn_data);
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
    */
    public static List<JTextPane> new_synergy_text_pane(String object_name, List<JTextPane> curr_item_data){
        List<Object> new_syn_data = MySQLAccess.obtain_synergy_data(object_name);
        for(int idx = 0; idx < new_syn_data.size(); idx++) {
            List<Object> indiv_syn_data = (List<Object>) new_syn_data.get(idx);

            // Create new JTextPane, initialize settings
            JTextPane new_syn_pane = new JTextPane();
            //new_syn_pane.setPreferredSize(new Dimension(150, 150));
            //Font font = new Font("Serif", Font.PLAIN, 16);
            //new_syn_pane.setFont(font);


            // Add the weapon mage to JTextPane
            int sf = 4; // scale factor
            BufferedImage syn_img = (BufferedImage) indiv_syn_data.get(2);
            Image scaled_item_img = syn_img.getScaledInstance(syn_img.getWidth() * sf, syn_img.getHeight() * sf, BufferedImage.TYPE_INT_ARGB);
            new_syn_pane.insertIcon(new ImageIcon(scaled_item_img));

            new_syn_pane.setPreferredSize(new Dimension(syn_img.getWidth() * sf + 50, syn_img.getHeight() * sf + 75));

            // Append text to JTextPane Document
            try {
                // Create string text
                //String text = "\nObject: " + indiv_syn_data.get(0) + "\nSynergy Effect: " + indiv_syn_data.get(1);
                String text = "\nSynergizes with:\n" + indiv_syn_data.get(3);

                Document doc = new_syn_pane.getDocument();
                doc.insertString(doc.getLength(), text, null);
            } catch (BadLocationException ex) {
                System.out.println(ex);
            }


            // Set border data
            new_syn_pane.setBackground(new Color(241, 241, 241));
            //new_syn_pane.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));

            // Add new item to list
            curr_item_data.add(new_syn_pane);
        }

        return curr_item_data;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static JScrollPane total_synergy_data_panel(List<JTextPane> curr_data){
        JPanel total_syn_pane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int cols = 4;
        int idx_y = 0;
        int idx_x = 0;
        for(JTextPane data : curr_data){
            c.gridx = idx_x;
            c.gridy = idx_y;
            total_syn_pane.add(data, c);
            idx_x += 1;
            if(idx_x > cols){
                idx_y += 1;
                idx_x = 0;
            }
        }
        JScrollPane scroll = new JScrollPane(total_syn_pane);
        scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder (), "Synergies", TitledBorder.CENTER, TitledBorder.TOP));
        scroll.setPreferredSize(new Dimension(100, 100));
        scroll.setAutoscrolls(true);
        return scroll;
    }

    public static void test_runs(){
        add_item("\'Ration\'", item_list);
        add_item("\'Medkit\'", item_list);
        add_item("\'Orange\'", item_list);
        add_item("\'Bottle\'", item_list);
        add_item("\'Bomb\'", item_list);
        add_item("\'C4\'", item_list);
        add_item("\'Molotov\'", item_list);
        add_item("\'Big_Boy\'", item_list);
        add_item("\'Backpack\'", item_list);
        add_item("\'Drill\'", item_list);
        add_item("\'Scope\'", item_list);
        add_item("\'Scouter\'", item_list);
        add_item("\'Orange\'", item_list);
        add_item("\'Clown_Mask\'", item_list);
        add_item("\'Aged_Bell\'", item_list);
        add_item("\'Bullet_Time\'", item_list);
        add_item("\'Decoy\'", item_list);
        add_item("\'Box\'", item_list);
        add_item("\'Jar_of_Bees\'", item_list);
        add_item("\'Daruma\'", item_list);
    }





    public static void main(String []args){
        UserInterface new_inst = new UserInterface();
        new_inst.add_weapon("\'Winchester\'", weapon_list);
        new_inst.test_runs();
        new_inst.run_user_interface();
        int test = 5;
    }
}
