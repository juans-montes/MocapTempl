package jsmocap.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;
import jsmocap.skeleton.Skeleton;

/**
 * Reads AMC motion file.
 * 
 * @author Michael Kipp
 */
public class AMCReader {

    @SuppressWarnings("unchecked")
	public Motion readMotion(File file, Skeleton skeleton) 
    {
    	Motion motion=new Motion();
    	motion.name_=file.getName();
    	motion.skeleton_=skeleton;
    	
        Pattern num = Pattern.compile("[0-9]+");
        BufferedReader in = null;
        try {
            HashMap<String, Bone> name2bone = skeleton.bones_;
            HashMap<String, ArrayList<Double>> name2data = new HashMap<String, ArrayList<Double>>();

            in = new BufferedReader(new FileReader(file));
            String line;

            // read data
            while ((line = in.readLine()) != null) {

                // ignore comments + acclaim info
                if (!(line.startsWith("#") || line.startsWith(":"))) {
                    if (!num.matcher(line).matches())
                        parseBoneKeyframe(line, name2data, name2bone);
                    else if(name2data.size()>0)
                    {
                    	motion.frames_.add((HashMap<String, ArrayList<Double>>)name2data.clone());
                    	name2data.clear();
                    }
                }
            }

            if(name2data.size()>0)
            	motion.frames_.add((HashMap<String, ArrayList<Double>>)name2data.clone());
            return motion;
        } catch (IOException ex) {
            Logger.getLogger(ASFReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(ASFReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private void parseBoneKeyframe(String line, HashMap<String, ArrayList<Double>> data,
            HashMap<String, Bone> name2bone) {
        String[] tok = line.split(" ");
        Bone b = name2bone.get(tok[0]);
        if (b == null && !tok[0].equals("root")) {
            System.out.println("WARNING: Bone " + tok[0] + " does not exist.");
            return;
        }
        ArrayList<Double> list = data.get(tok[0]);
        if (list == null) {
            list = new ArrayList<Double>();
            data.put(tok[0], list);
        }
        for (int i = 1; i < tok.length; i++) {
            try {
                Double fl = Double.parseDouble(tok[i]);
                list.add(fl);
            } catch (NumberFormatException e) {
                System.out.println("WARNING: Couldn't parse: " + tok[i]);
            }
        }

    }
}
