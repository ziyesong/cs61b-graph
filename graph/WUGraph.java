/* WUGraph.java */

package graph;

/**
 * The WUGraph class represents a weighted, undirected graph.  Self-edges are
 * permitted.
 */

public class WUGraph {

	protected list.DList VertexList;//Adjacency list representation of vertices
	protected dict.HashTableChained VertexTable = new dict.HashTableChained();
	protected dict.HashTableChained EdgeTable = new dict.HashTableChained();

	/**
	 * WUGraph() constructs a graph having no vertices or edges.
	 *
	 * Running time:  O(1).
	 */
	public WUGraph(){
		VertexList = new list.DList();
	}

	/**
	 * vertexCount() returns the number of vertices in the graph.
	 *
	 * Running time:  O(1).
	 */
	public int vertexCount(){
		return VertexList.length();
	}

	/**
	 * edgeCount() returns the number of edges in the graph.
	 *
	 * Running time:  O(1).
	 */
	public int edgeCount(){
		return EdgeTable.size();
	}

	/**
	 * getVertices() returns an array containing all the objects that serve
	 * as vertices of the graph.  The array's length is exactly equal to the
	 * number of vertices.  If the graph has no vertices, the array has length
	 * zero.
	 *
	 * (NOTE:  Do not return any internal data structure you use to represent
	 * vertices!  Return only the same objects that were provided by the
	 * calling application in calls to addVertex().)
	 *
	 * Running time:  O(|V|).
	 */
	public Object[] getVertices(){
		int size = VertexList.length();
		Object[] vertices = new Object[size];
		try{
			int index = 0;
			list.ListNode node = VertexList.front();
			while(true){
				vertices[index] = ((list.DList)node.item()).front().item();
				node = node.next();
				index++;
			}
		}catch(list.InvalidNodeException e){
		}
		return vertices;
	}

	/**
	 * addVertex() adds a vertex (with no incident edges) to the graph.  The
	 * vertex's "name" is the object provided as the parameter "vertex".
	 * If this object is already a vertex of the graph, the graph is unchanged.
	 *
	 * Running time:  O(1).
	 */
	public void addVertex(Object vertex){
		if (isVertex(vertex)){//already a vertex
			return;
		}else{
			VertexTable.insert(vertex, vertex);
			list.DList self = new list.DList();
			self.insertFront(vertex);
			VertexList.insertBack(self);
		}
	}

	/**
	 * removeVertex() removes a vertex from the graph.  All edges incident on the
	 * deleted vertex are removed as well.  If the parameter "vertex" does not
	 * represent a vertex of the graph, the graph is unchanged.
	 *
	 * Running time:  O(d), where d is the degree of "vertex".
	 */
	public void removeVertex(Object vertex){
		if (isVertex(vertex)){
			VertexTable.remove(vertex);
			try{
				list.ListNode node = VertexList.front();
				while(true){
					if(((list.DList)node.item()).front().item().equals(vertex)){
						list.DList neighborlist = (list.DList)node.item();
						try{
							list.ListNode curr = neighborlist.front();
							while(true){
								curr = curr.next();//start from the second node
								VertexPair pair = new VertexPair(vertex, ((Object[])curr.item())[0]);
								EdgeTable.remove(pair);
								//System.out.println("removing " + pair.object1 + " " + pair.object2);
								list.ListNode ref = (list.ListNode)(((Object[])curr.item())[1]);//reference
								if(ref != null){
									ref.remove();//remove reference from list
								}

							}
						}catch(list.InvalidNodeException e){	
						}
						node.remove();
						break;
					}
					node = node.next();
				}
			}catch(list.InvalidNodeException e){	
			}
		}
	}

	/**
	 * isVertex() returns true if the parameter "vertex" represents a vertex of
	 * the graph.
	 *
	 * Running time:  O(1).
	 */
	public boolean isVertex(Object vertex){
		return (VertexTable.find(vertex) != null);
	}

	/**
	 * degree() returns the degree of a vertex.  Self-edges add only one to the
	 * degree of a vertex.  If the parameter "vertex" doesn't represent a vertex
	 * of the graph, zero is returned.
	 *
	 * Running time:  O(1).
	 */
	public int degree(Object vertex){
		if (!isVertex(vertex)){
			return 0;
		}else{
			try{
				list.ListNode node = VertexList.front();
				while(true){
					if (((list.DList)node.item()).front().item().equals(vertex)){
						return ((list.DList)node.item()).length() - 1;
					}
					node = node.next();
				}
			}catch(list.InvalidNodeException e){
				return 0;//never reached
			}
		}
	}

	/**
	 * getNeighbors() returns a new Neighbors object referencing two arrays.  The
	 * Neighbors.neighborList array contains each object that is connected to the
	 * input object by an edge.  The Neighbors.weightList array contains the
	 * weights of the corresponding edges.  The length of both arrays is equal to
	 * the number of edges incident on the input vertex.  If the vertex has
	 * degree zero, or if the parameter "vertex" does not represent a vertex of
	 * the graph, null is returned (instead of a Neighbors object).
	 *
	 * The returned Neighbors object, and the two arrays, are both newly created.
	 * No previously existing Neighbors object or array is changed.
	 *
	 * (NOTE:  In the neighborList array, do not return any internal data
	 * structure you use to represent vertices!  Return only the same objects
	 * that were provided by the calling application in calls to addVertex().)
	 *
	 * Running time:  O(d), where d is the degree of "vertex".
	 */
	public Neighbors getNeighbors(Object vertex){
		int num = degree(vertex);
		if (num == 0){
			return null;
		}else{
			Neighbors neighbors = new Neighbors();
			Object[] obj = new Object[num];
			int[] weights = new int[num];
			try{
				int index = 0;
				list.ListNode node = VertexList.front();
				while(true){
					if(((list.DList)node.item()).front().item() == vertex){
						list.DList neighborlist = (list.DList)node.item();
						try{
							list.ListNode curr = neighborlist.front();
							while(true){
								curr = curr.next();
								obj[index] = ((Object[])curr.item())[0];
								weights[index] = (int)((Object[])curr.item())[2];
								index++;
							}
						}catch(list.InvalidNodeException e){

						}
						break;
					}
					node = node.next();
				}
			}catch(list.InvalidNodeException e){

			}
			neighbors.neighborList = obj;
			neighbors.weightList = weights;
			return neighbors;
		}
	}

	/**
	 * addEdge() adds an edge (u, v) to the graph.  If either of the parameters
	 * u and v does not represent a vertex of the graph, the graph is unchanged.
	 * The edge is assigned a weight of "weight".  If the edge is already
	 * contained in the graph, the weight is updated to reflect the new value.
	 * Self-edges (where u == v) are allowed.
	 *
	 * Running time:  O(1).
	 */
	public void addEdge(Object u, Object v, int weight){
		if (!isVertex(u) || !isVertex(v)){
			return;
		}
		if (isEdge(u, v)){//always remove first, then add edge
			removeEdge(u, v);
		}
		Edge edge = new Edge(u, v, weight);
		VertexPair pair = new VertexPair(u, v);
		EdgeTable.insert(pair, edge);//insert into EdgeTable
		try{
			list.ListNode node = VertexList.front();
			list.DList ulist = null;//initialize
			list.DList vlist = null;//initialize
			while(true){
				if (((list.DList)node.item()).front().item().equals(u)){
					ulist = (list.DList)node.item();
				}
				if (((list.DList)node.item()).front().item().equals(v)){
					vlist = (list.DList)node.item();
				}
				if(ulist != null && vlist != null){
					if(u.equals(v)){//self-edge
						Object[] obj = new Object[3];
						obj[0] = u;
						obj[1] = null;
						obj[2] = weight;
						ulist.insertBack(obj);
						break;
					}
					Object[] uobj = new Object[3];
					uobj[0] = v;
					uobj[2] = weight;
					Object[] vobj = new Object[3];
					vobj[0] = u;
					vobj[2] = weight;
					ulist.insertBack(uobj);
					vlist.insertBack(vobj);
					((Object[])ulist.back().item())[1] = vlist.back();
					((Object[])vlist.back().item())[1] = ulist.back();
					break;
				}
				node = node.next();
			}
		}catch(list.InvalidNodeException e){
		}
	}

	/**
	 * removeEdge() removes an edge (u, v) from the graph.  If either of the
	 * parameters u and v does not represent a vertex of the graph, the graph
	 * is unchanged.  If (u, v) is not an edge of the graph, the graph is
	 * unchanged.
	 *
	 * Running time:  O(1).
	 */
	public void removeEdge(Object u, Object v){
		if(!isEdge(u, v)){
			return;
		}
		VertexPair pair = new VertexPair(u, v);
		EdgeTable.remove(pair);//remove from table
		try{
			list.ListNode node = VertexList.front();
			while(true){
				if (((list.DList)node.item()).front().item().equals(u)){
					list.DList ulist = (list.DList)node.item();
					try{
						list.ListNode curr = ulist.front();
						while(true){
							curr = curr.next();
							if (((Object[])curr.item())[0].equals(v)){
								if(!u.equals(v)){
									((list.ListNode)((Object[])curr.item())[1]).remove();//remove (v,u)
								}
								curr.remove();//remove (u,v)
							}
						}
					}catch(list.InvalidNodeException e){
					}
				}
				node = node.next();
			}

		}catch(list.InvalidNodeException e){
		}
	}

	/**
	 * isEdge() returns true if (u, v) is an edge of the graph.  Returns false
	 * if (u, v) is not an edge (including the case where either of the
	 * parameters u and v does not represent a vertex of the graph).
	 *
	 * Running time:  O(1).
	 */
	public boolean isEdge(Object u, Object v){
		VertexPair pair = new VertexPair(u, v);
		return EdgeTable.find(pair) != null;
	}

	/**
	 * weight() returns the weight of (u, v).  Returns zero if (u, v) is not
	 * an edge (including the case where either of the parameters u and v does
	 * not represent a vertex of the graph).
	 *
	 * (NOTE:  A well-behaved application should try to avoid calling this
	 * method for an edge that is not in the graph, and should certainly not
	 * treat the result as if it actually represents an edge with weight zero.
	 * However, some sort of default response is necessary for missing edges,
	 * so we return zero.  An exception would be more appropriate, but
	 * also more annoying.)
	 *
	 * Running time:  O(1).
	 */
	public int weight(Object u, Object v){
		if (!isEdge(u, v)){
			return 0;
		}else{
			VertexPair pair = new VertexPair(u, v);
			return ((Edge)EdgeTable.find(pair).value()).weight;
		}
	}

	public static void main(String[] args) {
		WUGraph g = new WUGraph();
		g.addVertex(0);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);
		g.addVertex(7);
		g.addVertex(8);
		g.addVertex(9);
		//g.addEdge(3, 7, 4);
		g.addEdge(4, 7, 5);
		//g.addEdge(3, 3, 7);
		//g.addEdge(3, 3, 7);
		//g.addEdge(3, 3, 7);
		g.removeVertex(7);
		//g.addEdge(1, 4, 7);
		//g.addEdge("C", "D", 3);
		//g.addEdge("D", "E", 4);
		//g.addEdge("B", "E", 5);
		//g.addEdge("B", "C", 6);
		//g.addEdge("B", "D", 4);
		//g.removeVertex("A");
		//g.removeEdge("B", "D");


		//System.out.println(g.isEdge(3, 3));
	}

}