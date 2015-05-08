package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;

public abstract class AbstractCacheTool extends ToolGeneric{
	private List<GLSelectableObject> fullList = new ArrayList<GLSelectableObject>();
	private Iterator<GLSelectableObject> fullListIterator;

	public void onNewFrameAbstractCacheTool(){
		fullListIterator = fullList.iterator();
	}
	
	protected void removeFromCache(List<GLSelectableObject> currentLine){
		fullList.removeAll(currentLine);
	}
	
	protected void removeFromCache(GLSelectableObject currentLine){
		fullList.remove(currentLine);
	}

	protected GLSelectableObject getNewObject(float[] pos){
		return getNewObject(pos[0], pos[1], pos[2]);
	}
	
	protected GLSelectableObject getNewObject(float x, float y, float z){
		if(fullListIterator.hasNext()){
			GLSelectableObject cube = fullListIterator.next();
			cube.getModel()[12] = x;
			cube.getModel()[13] = y;
			cube.getModel()[14] = z;
			return cube;
		}
		GLSelectableObject cube = new GLSelectableObject(x,y,z);
        cube.onSurfaceCreated(world.vertexShader, world.passthroughShader, world.passthroughShader);
        return cube;
	}
	
}
