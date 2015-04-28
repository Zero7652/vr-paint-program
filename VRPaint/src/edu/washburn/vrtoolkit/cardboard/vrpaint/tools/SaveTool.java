package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff.GLSelectableObject;


public class SaveTool extends ToolGeneric {
	private boolean clear = false;
	private SaveFile file = SaveFile.SAVE01;
	@Override
    public boolean processDpad(float x, float y){
		if(x<-.5){
			file = file.previous();
		}
		if(x>.5){
			file = file.next();
		}
		world.getMain().getOverlayView().show3DToast(file.getName());
		return true;
    }
	
	@Override
    public boolean processButtonA(boolean pressed){
		if(pressed){
			try{
				FileOutputStream output = world.getMain().getApplicationContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
				OutputStreamWriter osw = new OutputStreamWriter(output);
				BufferedWriter writer = new BufferedWriter(osw);
				for(int i = 0;i<world.cubes.size();i++)
				{
					for(int k = 0;k<16;k++)
					{
						writer.write(Float.toString(world.cubes.get(i).getModel()[k]));
						writer.newLine();
					}
				}
				writer.close();
				osw.close();
				output.close();
				world.getMain().getOverlayView().show3DToast("Finished saving file: " + file.getName());
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		return pressed;
    }

	@Override
    public boolean processButtonB(boolean pressed){
		if(pressed){
			this.moving = true;
			world.getMain().getOverlayView().show3DToast("Finished loading file: " + file.getName());
		}
		return true;
    }


	@Override
    public boolean processButtonY(boolean pressed){
		if(pressed){
			clear = true;
			world.getMain().getOverlayView().show3DToast("Cleared the world");
		}
		return true;
    }
	
	public enum SaveFile {
		SAVE01("Save01.vrp"),
		SAVE02("Save02.vrp"),
		SAVE03("Save03.vrp");

		private final String name;
		private static SaveFile[] vals = values();
		
		SaveFile(String name){
			this.name = name;
		}
		
		public SaveFile next(){
	        return vals[(this.ordinal()+1) % vals.length];
	    }
	    public SaveFile previous(){
	        return vals[(this.ordinal()+vals.length-1) % vals.length];
	    }

		public String getName() {
			return name;
		}
	}
	@Override
	public void onNewFrame(HeadTransform headTransform){
		if(clear){
			for(GLSelectableObject cube : world.cubes){
				GLES20.glDeleteProgram(cube.getProgram());
			}
			world.cubes.clear();
		}
        if(moving) {
        	loadCubes();
            moving = false;
        }
	}

	public void loadCubes()
	{
		try
		{
			for(GLSelectableObject cube : world.cubes){
				GLES20.glDeleteProgram(cube.getProgram());
			}
			world.cubes.clear();
			String[] temp = new String[16];
			int i = 0;
			float[] tempFloat = new float[16];
			Log.i("test", "test-1");
			FileInputStream input = world.getMain().getApplicationContext().openFileInput(file.getName());
			Log.i("test", "test0");
	        InputStreamReader isr = new InputStreamReader(input);
	        BufferedReader bufferedReader = new BufferedReader(isr);
            Log.i("test", "test1");
            while ((temp[i] = bufferedReader.readLine()) != null ) {
            	tempFloat[i] = Float.parseFloat(temp[i]);
                Log.i("test", "test2");
            	if(i == 15)
                {
            		GLSelectableObject tempCube = world.new GLSelectableObject(0, 0, 0);
                	tempCube.onSurfaceCreated(world.vertexShader, world.passthroughShader, world.passthroughShader);
            		for(int k=0;k<16;k++)
                	{
                		tempCube.getModel()[k] = tempFloat[k];
                	}
                	world.cubes.add(tempCube);
                	i=i%15;
                }
            	else {
            		i++;
            	}
            }
            Log.i("test", "test3");
            bufferedReader.close();
            isr.close();
            input.close();
        }
		catch (FileNotFoundException e) {
        } catch (IOException e) {
        }		
	}
}
