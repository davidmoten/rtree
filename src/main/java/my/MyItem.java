package my;

public class MyItem {

	private long id;
	public float[] values;
	private ItemType itemType = ItemType.S;
	
	private double cosine;
	
	public void calculateCosine() {
		double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < values.length; i++) {
	        dotProduct += values[i] / values.length;
	        normA += Math.pow(values[i], 2);
	        normB += Math.pow(1 / values.length, 2);
	    }   
	    cosine = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	public MyItem() {
		super();
	}
	
	public MyItem(MyItem other) {
		this.id = other.id;
		this.itemType = other.itemType;
		this.values = other.values.clone();
	}
	
	public MyItem(float[] values) {
		this.values = values;
	}
	
	public MyItem(long id, ItemType itemType) {
		super();
		this.id = id;
		this.itemType = itemType;
	}

	public MyItem(long id, float[] values, ItemType itemType) {
		super();
		this.id = id;
		this.values = values;
		this.itemType = itemType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			builder.append(values[i] + "\t");
		}
		return builder.toString();
	}

	/*public Text valuesToText(){
		String text = "[ ";
		for(int i=0;i<values.length;i++){
			text += values[i];
			if(i+1<values.length)
				text += " , ";
		}
		text += " ]";
		return new Text(text);
	}*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) id;
		result = prime * result
				+ ((itemType == null) ? 0 : itemType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyItem other = (MyItem) obj;
		if (id != other.id)
			return false;
		if (itemType != other.itemType)
			return false;
		return true;
	}
/*
	@Override
	public void readFields(DataInput input) throws IOException {
		id = input.readLong();		
		if(input.readInt()==0)
			itemType = ItemType.W;
		else
			itemType = ItemType.S;
		
		int valuesNum = input.readInt();
		values = new float[valuesNum];
		for (int i = 0; i < valuesNum; i++) {
			values[i] = input.readFloat();
		}
		
		/*
		ArrayWritable aw = new ArrayWritable(DoubleWritable.class);		
		aw.readFields(input);
		
		Writable[] a = aw.get();
		values = new double[a.length];
		for(int i = 0; i < a.length;i++)
			values[i] = (DoubleWritable) a[i];
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(id);
		output.writeInt(itemType.getValue());
		//ArrayWritable aw = new ArrayWritable(DoubleWritable.class,values);		
		//aw.write(output);
		output.writeInt(values.length);
		for (int i = 0; i < values.length; i++) {
			output.writeFloat(values[i]);
		}
	}
*/
	public double getCosine() {
		return cosine;
	}

}
