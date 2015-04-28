package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;




public class OpenGlControl {
	public enum Tools {
		NOT_DRAWING(new ToolGeneric()),
		FREE_DRAWING(new FreeDrawTool()),
		LINE(new LineTool()),
		CIRCLE(new ToolGeneric()),
		POLYGON(new ToolGeneric());

		private final ToolGeneric tool;
		private static Tools[] vals = values();
		
		Tools(ToolGeneric moveObject){
			tool = moveObject;
		}
		
		public ToolGeneric getTool() {
			return tool;
		}

		public Tools next(){
	        return vals[(this.ordinal()+1) % vals.length];
	    }
	    public Tools previous(){
	        return vals[(this.ordinal()+vals.length-1) % vals.length];
	    }
	}
    
	private ToolGeneric freeDrawing = new ToolGeneric();
	private ToolGeneric moveUser = new ToolGeneric(false);

	public boolean processMove(float x, float y, float [] vector){
//		if(moveUser.isMoving() && vector != null){
//			moveUser.setPos(vector);
//			return true;
//		} else if(freeDrawing.isMoving()){
//			freeDrawing.setMoveZ(y);
//			freeDrawing.setScale(1+(x/100));
//			return true;
//		}
		return false;
	}

	public boolean isNewFrameControl(){
		return false;
//		return freeDrawing.isMoving() || moveUser.isMoving();
	}
}
