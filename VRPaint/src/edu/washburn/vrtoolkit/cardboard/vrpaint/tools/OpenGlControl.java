package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;




public class OpenGlControl {
	public enum Tools {
		NOT_DRAWING("No Drawing Mode",new ToolGeneric()),
		FREE_DRAWING("Free Drawing Mode", new FreeDrawTool()),
		LINE("Line Drawing Mode", new LineTool()),
		CIRCLE("Circle Drawing Mode", new CircleTool()),
		POLYGON("Polygon Drawing Mode", new PolygonTool()),
		BURST("Starburst Drawing Mode", new StarburstTool()),
		SAVE("Save Drawing Mode", new SaveTool());

		private final String toolText;
		private final ToolGeneric tool;
		private static Tools[] vals = values();
		
		Tools(String toolText, ToolGeneric moveObject){
			this.toolText = toolText;
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

		public String getToolText() {
			return toolText;
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
