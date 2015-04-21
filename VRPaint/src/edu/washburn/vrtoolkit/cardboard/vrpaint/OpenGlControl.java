package edu.washburn.vrtoolkit.cardboard.vrpaint;

import java.util.ArrayList;
import java.util.List;


public class OpenGlControl {
	public final static float episolon = 0.000001f;
	private List<MoveObject> list = new ArrayList<MoveObject>();
	private int listIndex = 0;
	private boolean listChanged = true;
	private MoveObject objectTool = new MoveObject("Now creating Objects");
	private MoveObject moveUser = new MoveObject("Free range mode");
	private MoveObject line = new MoveObject("Create Lines");
	private MoveObject save = new MoveObject("Save Tool \nA = save\nX = load");
	
	public OpenGlControl(){
		list.add(moveUser);
		list.get(listIndex).setActive(true);
		list.add(objectTool);
		list.add(line);
		list.add(save);
	}

	public boolean processMove(float x, float y, float [] vector){
		if(moveUser.isActive() && vector != null){
			moveUser.setMoveX(vector[0]);
			moveUser.setMoveY(vector[1]);
			moveUser.setMoveZ(vector[2]);
			return true;
		} else if(objectTool.isActive()){
			objectTool.setMoveZ(y);
			objectTool.setScale(1+(x/100));
			return true;
		}
		return false;
	}
	public void controlForward(){
		setListChanged(true);
		list.get(listIndex).setActive(false);
		listIndex++;
		if(listIndex >= list.size())
			listIndex = 0;
		list.get(listIndex).setActive(true);
		
	}
	public void controlBack(){
		setListChanged(true);
		list.get(listIndex).setActive(false);
		listIndex--;
		if(listIndex < 0)
			listIndex = list.size()-1;
		list.get(listIndex).setActive(true);
	}
	public String getNewText(){
		return list.get(listIndex).getNewText();
	}
	public MoveObject getObjectTool() {
		return objectTool;
	}
	public void setObjectTool(MoveObject moveObject) {
		this.objectTool = moveObject;
	}

	public boolean isNewFrameControl(){
		return objectTool.isActive() || moveUser.isActive();
	}
	public MoveObject getMoveUser() {
		return moveUser;
	}

	public void setMoveUser(MoveObject moveUser) {
		this.moveUser = moveUser;
	}
	public boolean isListChanged() {
		return listChanged;
	}

	public void setListChanged(boolean listChanged) {
		this.listChanged = listChanged;
	}
	public class MoveObject{
		private String newText = null;
		private boolean active = false;
		private boolean creating = false;
		private float moveX = 0;
		private float moveY = 0;
		private float moveZ = 0;
		private float scale = 1;
		public MoveObject(String text){
			setNewText(text);
		};
		public boolean isActive(){
			return     active;
//					&&
//					(
//					   Math.abs(moveX) > episolon
//					|| Math.abs(moveY) > episolon
//					|| Math.abs(moveZ) > episolon
//					);
		}
		public float getMoveX() {
			return moveX;
		}
		public void setMoveX(float moveX) {
			this.moveX = moveX;
		}
		public float getMoveY() {
			return moveY;
		}
		public void setMoveY(float moveY) {
			this.moveY = moveY;
		}
		public float getMoveZ() {
			return moveZ;
		}
		public void setMoveZ(float moveZ) {
			this.moveZ = moveZ;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		public float getScale() {
			return scale;
		}
		public void setScale(float scale) {
			this.scale = scale;
		}
		public String getNewText() {
			return newText;
		}
		public void setNewText(String newText) {
			this.newText = newText;
		}
		public boolean isCreating() {
			return creating;
		}
		public void setCreating(boolean creating) {
			this.creating = creating;
		}
	}
}
