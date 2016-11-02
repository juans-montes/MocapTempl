package jsmocap.view;

import java.util.ArrayList;









import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import jsmocap.skeleton.Annotation;
import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;

public class MocapScene 
{
	private final static double SPHERE_RADIUS = 0.4;
	private final static double CYLINDER_RADIUS = 0.2;
	
	private SubScene skeleton_scene_;
	private Group root_;
	private Group gskeleton_;
	
	private PerspectiveCamera camera_;

	private Motion motion_=null;
	
	private boolean timer_running_;
	private AnimationTimer timer_;
	private int current_frame_=0;
	
	private double startX = 0;
	private double startY = 0;
	private Affine viewingRotate = new Affine();
	
	private Rotate viewingRotX = new Rotate(0, 0,0,0, Rotate.X_AXIS);      
    private Rotate viewingRotY = new Rotate(0, 0,0,0, Rotate.Y_AXIS);
	
    private ArrayList<AnimationWrapper> shapes_=new ArrayList<AnimationWrapper>();
    
    private PhongMaterial redMaterial;
    private PhongMaterial greenMaterial;
    
    private PhongMaterial blackMaterial;
    private PhongMaterial whiteMaterial;
    
	public MocapScene(Controller controller)
	{
		controller.mocapscene_=this;
		
		gskeleton_=new Group();
		
		redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
		
        greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
		
        blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.DARKGRAY);
        blackMaterial.setSpecularColor(Color.DARKGRAY);
        
        whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.WHITE);
        
		timer_ = new AnimationTimer() {
            @Override
            public void handle(long l) 
            {
            	current_frame_=(current_frame_+1)%motion_.frames_.size();
            	
            	greenMaterial.setDiffuseColor(Color.DARKGREEN);
                greenMaterial.setSpecularColor(Color.GREEN);
            	
                ArrayList<String> labels=new ArrayList<String>();
            	for(int i=0; i<motion_.annotations_.size(); ++i)
            	{
            		Annotation annotation=motion_.annotations_.get(i);
            		for(int j=0; j<annotation.windows_.size(); ++j)
            		{
            			if(annotation.windows_.get(j).x<=current_frame_ && annotation.windows_.get(j).y>=current_frame_)
            			{
            				greenMaterial.setDiffuseColor(Color.DARKBLUE);
                            greenMaterial.setSpecularColor(Color.BLUE);
                            labels.add(annotation.name_);
                            break;
            			}
            		}
            	}
            	
            	controller.cdisplay_.setList(labels);
            	motion_.updateSkeleton(current_frame_);
            	for(int i=0; i<shapes_.size(); ++i)
            		shapes_.get(i).update();
                }
            };
            
		root_=new Group();
		skeleton_scene_=new SubScene(root_, 800, 600, true, SceneAntialiasing.BALANCED);
		skeleton_scene_.setFill(Color.TRANSPARENT);
		
		AmbientLight ambient = new AmbientLight();
        ambient.setColor(Color.rgb(200, 200, 200,0.5));

        PointLight point = new PointLight   ();
        point.setColor(Color.rgb(255, 255, 255,1));
        point.setLayoutX(400);
        point.setLayoutY(100);
        point.setTranslateZ(-1100);

        root_.getChildren().addAll(ambient, point);
	    
		int rsize = 10;
		for(int i=-10; i<10; ++i)
		{
			for(int j=-10; j<10; ++j)
			{
				Box rectangle=new Box(rsize, rsize, 1);//(rsize*i, rsize*j, rsize, rsize);
				rectangle.getTransforms().add(new Translate(rsize*i, -1, -rsize*j));
				rectangle.getTransforms().add(new Rotate(90, 0, 0, 0, Rotate.X_AXIS));
				
				//rectangle.setStroke(new Color(0.4, 0.4, 0.4, 1));
				if((i+j)%2==0)
					rectangle.setMaterial(whiteMaterial);
				else
					rectangle.setMaterial(blackMaterial);
				
				root_.getChildren().add(rectangle);
			}
		}
		
		root_.getChildren().add(gskeleton_);
		
		camera_=new PerspectiveCamera(true);
		camera_.setTranslateZ(-150);
		camera_.setTranslateY(15);
		camera_.setRotate(180);
        camera_.setNearClip(0.1);
        camera_.setFarClip(500);
        skeleton_scene_.setCamera(camera_);
        
        root_.getTransforms().add(viewingRotate);
        
        skeleton_scene_.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) { 
                    viewingRotX.setAngle((startY - event.getSceneY())/10); 
                    viewingRotY.setAngle((event.getSceneX() - startX)/10);                                        
                    viewingRotate.append(viewingRotX.createConcatenation(viewingRotY));
                }
                     
                startX = event.getSceneX();
                startY = event.getSceneY();
           }
        });
        
        skeleton_scene_.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                startX = event.getSceneX();
                startY = event.getSceneY();
            }
        }); 
        
        
	}
	
	public void resumePause()
	{
		if(motion_!=null)
		{
			if(timer_running_)
				timer_.stop();
			else
				timer_.start();
			timer_running_=!timer_running_;
		}
	}
	
	public void setup(Motion motion)
	{
		timer_running_=false;
		timer_.stop();
		current_frame_=0;
		gskeleton_.getChildren().clear();
		motion_=motion;
		
		Sphere sphere=new Sphere(SPHERE_RADIUS);
        Affine aff=new Affine(matToArray(motion_.skeleton_.global_), MatrixType.MT_3D_4x4, 0);
        sphere.getTransforms().add(aff);
        sphere.setMaterial(redMaterial);
        
        gskeleton_.getChildren().add(sphere);
        
        AnimationWrapper aw=new AnimationWrapper();
        aw.bone_=null;
        aw.cylinder_=null;
        aw.parent_=null;
        aw.skeleton_=motion_.skeleton_;
        aw.sphere_=sphere;
        shapes_.add(aw);
        
        for(int i=0; i<motion_.skeleton_.children_.size(); ++i)
        	addNode(motion_.skeleton_.children_.get(i), null);
        
        timer_running_=true;
        timer_.start();
	}
	
	public void addNode(Bone bone, Bone parent)
	{
		Sphere sphere=new Sphere(SPHERE_RADIUS);
        Affine aff=new Affine(matToArray(bone.global_), MatrixType.MT_3D_4x4, 0);
        sphere.getTransforms().add(aff);
        sphere.setMaterial(redMaterial);
        
        gskeleton_.getChildren().add(sphere);
        
        Vector4d source=new Vector4d(0,0,0,1);
        if(parent!=null)
        	parent.global_.transform(source);
        else
        	motion_.skeleton_.global_.transform(source);
        
        Vector4d target=new Vector4d(0,0,0,1);
        bone.global_.transform(target);
        
        Vector4d cyl_trans=new Vector4d();
        cyl_trans.add(source, target);
        cyl_trans.scale(0.5);
        
        Matrix4d cyl_matrix=new Matrix4d();
        cyl_matrix.setIdentity();
        
        Vector4d dir=new Vector4d();
        dir.sub(target, source);
        dir.normalize();
        
        Vector3d axis=new Vector3d(dir.x, dir.y, dir.z);
        double angle=-Math.acos(axis.dot(new Vector3d(0,1,0)));
        
        axis.cross(new Vector3d(dir.x, dir.y, dir.z), new Vector3d(0,1,0));
        axis.normalize();
        
        
        AxisAngle4d aa=new AxisAngle4d(axis.x, axis.y, axis.z, angle);
        
        cyl_matrix.setRotation(aa);
        cyl_matrix.setTranslation(new Vector3d(cyl_trans.x, cyl_trans.y, cyl_trans.z));
        
        
        Cylinder cylinder=new Cylinder(CYLINDER_RADIUS, bone.length_);
        cylinder.getTransforms().add(new Affine(matToArray(cyl_matrix), MatrixType.MT_3D_4x4, 0));
        cylinder.setMaterial(greenMaterial);
        gskeleton_.getChildren().add(cylinder);
        
        AnimationWrapper aw=new AnimationWrapper();
        aw.bone_=bone;
        aw.cylinder_=cylinder;
        aw.parent_=parent;
        aw.skeleton_=motion_.skeleton_;
        aw.sphere_=sphere;
        shapes_.add(aw);
        
        for(int i=0; i<bone.children_.size(); ++i)
        	addNode(bone.children_.get(i), bone);
	}
	
	public SubScene getSubScene()
	{
		return skeleton_scene_;
	}
	
	public static double[] matToArray(Matrix4d mat)
	{
		double arr[]=new double[16];
		arr[0]=mat.m00;
		arr[1]=mat.m01;
		arr[2]=mat.m02;
		arr[3]=mat.m03;
		arr[4]=mat.m10;
		arr[5]=mat.m11;
		arr[6]=mat.m12;
		arr[7]=mat.m13;
		arr[8]=mat.m20;
		arr[9]=mat.m21;
		arr[10]=mat.m22;
		arr[11]=mat.m23;
		arr[12]=mat.m30;
		arr[13]=mat.m31;
		arr[14]=mat.m32;
		arr[15]=mat.m33;
		return arr;
	}
}
