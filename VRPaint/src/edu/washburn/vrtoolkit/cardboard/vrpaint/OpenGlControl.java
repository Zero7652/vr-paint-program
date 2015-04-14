package edu.washburn.vrtoolkit.cardboard.vrpaint;

import android.util.Log;

public class OpenGlControl {
	public final static float episolon = 0.000001f;
	private MoveObject moveObject = new MoveObject();
	private MoveObject moveUser = new MoveObject(false);

	public boolean processX(float x){
		if(moveUser.isMoving()){
			moveUser.setMoveX(x);
			return true;
		} else if(moveObject.isMoving()){
			return true;
		}
		return false;
	}
	public boolean processY(float y){
		if(moveUser.isMoving()){
			moveUser.setMoveZ(y);
			return true;
		} else if(moveObject.isMoving()){
			moveObject.setMoveZ(y);
			return true;
		}
		return false;
	}
	public MoveObject getMoveObject() {
		return moveObject;
	}

	public void setMoveObject(MoveObject moveObject) {
		this.moveObject = moveObject;
	}

	public boolean isNewFrameControl(){
		return moveObject.isMoving() || moveUser.isMoving();
	}
	public MoveObject getMoveUser() {
		return moveUser;
	}

	public void setMoveUser(MoveObject moveUser) {
		this.moveUser = moveUser;
	}
	public class MoveObject{
		boolean moving = true;
		float moveX = 0;
		float moveY = 0;
		float moveZ = 0;
		public MoveObject(){};
		public MoveObject(boolean moving){
			this.moving = moving;
		}
		public boolean isMoving(){
			return     moving == true;
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
		public void setMoving(boolean moving) {
			this.moving = moving;
		}
	}
}
