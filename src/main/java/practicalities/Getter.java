package practicalities;

public abstract class Getter<DataType, Operand> {
	public Operand op;
	
	public Getter(Operand op) {
		this.op = op;
	}
	
	public abstract DataType get();
}
