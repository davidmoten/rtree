package my;

public enum ItemType {
	
	W(0), S(1), W_InTopK(2);
    private final int value;

    private ItemType(int value) {
        this.value = value;
    }

    
	public int getValue() {
		return value;
	}
}
