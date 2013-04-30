package graph;

public class Edge {
	protected Object v1;
	protected Object v2;
	protected int weight;
	
	public Edge(Object object1, Object object2, int w){
		v1 = object1;
		v2 = object2;
		weight = w;
	}
}
