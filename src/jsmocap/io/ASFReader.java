package jsmocap.io;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.vecmath.Vector3d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Skeleton;

public class ASFReader 
{

    private Skeleton readRoot(BufferedReader in) throws IOException 
    {
        Skeleton root = new Skeleton();
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("order")) {
                root.order_ = readDOF(line);
            }
            if (line.startsWith("position")) {
                root.position_ = getVector(line);
            }
            if (line.startsWith("orientation")) {
                root.orientation_ = getVector(line);
            }
            if (line.startsWith("axis")) {
            	String[] ltok=line.split(" ");
            	root.rot_order_ = readAxisDOF(ltok[ltok.length-1]);
            }
            if (line.startsWith(":bonedata")) {
                break;
            }
        }
        root.precompute();
        return root;
    }

    private Bone.Type[] readDOF(String line) {
        String[] tok = line.split(" ");
        if (tok.length > 1) {
            Bone.Type[] dof = new Bone.Type[tok.length - 1];
            for (int i = 1; i < tok.length; ++i) {
                String t = tok[i].toLowerCase();
                if (t.startsWith("r")) {
                    dof[i - 1] = t.equals("rx") ? Bone.Type.RX : (t.equals("ry") ? Bone.Type.RY : Bone.Type.RZ);
                } else {
                    dof[i - 1] = t.equals("tx") ? Bone.Type.TX : (t.equals("ty") ? Bone.Type.TY : Bone.Type.TZ);
                }
            }
            return dof;
        }
        return new Bone.Type[0];
    }

    private Bone.Type[] readAxisDOF(String line)
    {
    	Bone.Type[] dof=new Bone.Type[line.length()];
        String sdof=line.toLowerCase();
        for (int i = 0; i < sdof.length(); ++i) {
            char t = sdof.charAt(i);
                dof[i] = t=='X' ? Bone.Type.RX : (t=='Y' ? Bone.Type.RY : Bone.Type.RZ);
        }
        return dof;
    }
    
    private Bone readBone(BufferedReader in) throws IOException {

        Bone bone = new Bone();
        
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("id")) {
                bone.id_=getInt(line);
            }
            else if (line.startsWith("name")) {
                bone.name_ = trimKeyword(line);
            } 
            else if (line.startsWith("direction")) {
                bone.direction_ = getVector(line);
                if(bone.direction_.length()<.00000001)
                	throw new IOException("Bone " + bone.name_ + " has (0,0,0) direction!");
                bone.direction_.normalize();
            } 
            else if (line.startsWith("length")) {
                bone.length_= getFloat(line);
            } 
            else if (line.startsWith("axis")) {
            	String[] ltok=line.split(" ");
            	bone.rot_order_ = readAxisDOF(ltok[ltok.length-1]);
                bone.axis_ = getVector(line);
            } 
            else if (line.startsWith("dof")) {
                bone.dofs_ = readDOF(line);
            } 
            else if (line.startsWith("end"))
            {
                break;
            }
        }
        bone.precompute();
        return bone;
    }

    private void readHierarchy(BufferedReader in, HashMap<String, Bone> name2bone, Skeleton root) throws IOException {
        root.bones_=name2bone;
    	
    	String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.equals("end")) {
                break;
            }
            int pos = line.indexOf(' ');
            if ((line.length() > 0) && (pos > -1)) {
                String name = line.substring(0, pos);
                
                Bone parent = name2bone.get(name);
                if (!name.equals("root") && parent == null) {
                    System.out.println("WARNING! Parent \"" + name + "\" not found (:hierarchy)");
                }
                else 
                {
                    StringTokenizer tok = new StringTokenizer(trimKeyword(line));
                    while (tok.hasMoreElements()) {
                        String n = tok.nextToken();
                        Bone el = name2bone.get(n);
                        if (el == null) {
                            System.out.println("WARNING! Bone \"" + n + "\" not found (:hierarchy).");
                        } else {
                            if(name.equals("root"))
                            	root.children_.add(el);
                            else
                            parent.children_.add(el);
                        }
                    }
                }
            }
        }
    }

    private String trimKeyword(String line) {
        return line.substring(line.indexOf(' ') + 1);
    }

    private Vector3d getVector(String st) {
        String[] tok = st.split(" ");
        double x = Double.parseDouble(tok[1]);
        double y = Double.parseDouble(tok[2]);
        double z = Double.parseDouble(tok[3]);
        return new Vector3d(x, y, z);
    }

    private float getFloat(String str) {
        str = trimKeyword(str);
        return Float.parseFloat(str);
    }

    private int getInt(String str) {
        str = trimKeyword(str);
        return Integer.parseInt(str);
    }
    
    public Skeleton readSkeleton(File file) throws IOException 
    {
        Skeleton root = new Skeleton();
        HashMap<String, Bone> name2bone = new HashMap<String, Bone>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        boolean bonedata = false;
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith((":root"))) {
                root = readRoot(in);
                bonedata = true;
            }
            if (bonedata && (line.startsWith("begin"))) {
                Bone bone = readBone(in);
                name2bone.put(bone.name_, bone);
            }
            if (line.startsWith(":hierarchy")) {
                bonedata = false;
                readHierarchy(in, name2bone, root);
            }
        }
        root.updateGlobal();
        return root;
    }
}
