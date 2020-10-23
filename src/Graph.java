import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;
import java.util.Stack;

public class Graph extends Digraph {
	
	private Stack<Vertex> st1;
	
    public Graph() {
		st1 = new Stack<Vertex>( );
    }
  
    @Override public void add_arc( Integer id1, Integer id2) {
		System.out.println("Operação não permitida: Adição de arco.");
     }
    
	@Override public boolean is_acyclic( ) {
		// fazer
		return true;
	}

	@Override public boolean is_bipartite( ) {
		for( Vertex v1 : vertex_set.values( ) ) {
			if( v1.ind_set == 0 ) {
				v1.ind_set = 1;
				if( ! bipartite_visit( v1 ) )
					return false;
			}
		}
		System.out.print( "\n É bipartido " );
		return true;
	}
	private boolean bipartite_visit( Vertex v1 ) {
		for( Vertex v2 : v1.nbhood.values( ) ) {
			if( v2.ind_set == 0 ) {
				v2.ind_set = - v1.ind_set;
				if( ! bipartite_visit( v2 ) ) 
					return false;
			}
			if( v2.ind_set == v1.ind_set ) {
				// encontrar um ciclo ímpar
				System.out.printf( "\n Não é bipartido " + v1.id + " " + v2.id );
				return false;
			}
		}
		return true;
	}

	/* Função para construir um partially ordered set para depois descobrir se o grafo é um
	* Comparability Graph
	*/
	public Digraph poset(){
    	Digraph d1 = new Digraph();
    	for (Vertex v : vertex_set.values()){
    		for (Vertex v1 : v.nbhood.values()) {
//				System.out.printf("v = %d   v1 = %d\n", v.id, v1.id);
				if (d1.vertex_set.get(v.id) == null ){
					d1.add_arc(v.id,v1.id);
					d1.vertex_set.get(v1.id).setChega(true);
				}
				if (!d1.vertex_set.get(v.id).chega){
					if (d1.vertex_set.get(v1.id) == null ){
						d1.add_arc(v.id,v1.id);
						d1.vertex_set.get(v1.id).setChega(true);
					}
					else if (d1.vertex_set.get(v1.id).nbhood.get(v.id) == null){
						d1.add_arc(v.id,v1.id);
						d1.vertex_set.get(v1.id).setChega(true);
					}
				}else {
					if (d1.vertex_set.get(v1.id) == null ){
						d1.add_arc(v1.id,v.id);
						d1.vertex_set.get(v.id).setChega(true);
					}
					else if (d1.vertex_set.get(v.id).nbhood.get(v1.id) == null){
						d1.add_arc(v1.id,v.id);
						d1.vertex_set.get(v.id).setChega(true);
					}
				}
			}
		}
    	return d1;
	}

	/* Função para saber se um vértice tem um arco chegando e um saindo.
	* Se existir o arco a->b e o arco b->c, para o grafo ser Comparability, é necessário existir o arco a->c
	*/
	private Vertex isTheEnd(Vertex v){
    	if (v.nbhood.values().isEmpty()) {
			return v;
		}
    	int i = 0;
    	for (Vertex v1 : v.nbhood.values()){
    		i = v1.id;
		}
    	return v.nbhood.get(i);
	}
	/* Função para descobrir se um grafo é um Comparability Graph.
	* Para ser um Comparability Graph o grafo precisa ser reflexivo, anti-simétrico e transitivo
	* Anti-Simétrico - Se existe a->b, b->a não existe.
	* Transitivo - Se existe o arco a->b e o arco b->c, então existe a->c.
	*/
	public boolean isComparability(){
    	Digraph d1 = this.subjacent().poset();

    	for (Vertex v : d1.vertex_set.values()){
    		for (Vertex v1 : v.nbhood.values()){
				if (v1.nbhood.containsKey(v.id)){
					return false;
				}
    			if (!isTheEnd(v1).id.equals(v1.id)){
    				if (!v.nbhood.containsKey(isTheEnd(v1).id)){
    					return false;
					}
				}
			}
		}
    	return true;
	}
	/* Função para descobrir se um grafo é um Permutation Graph.
	* Para um grafo ser um Permutation Graph, ele e seu complemento precisam ser Comparability Graphs.
	* */

	public boolean isPermutation(){
    	if (this.isComparability() && this.reverse().subjacent().isComparability()){
    		return true;
		}
    	return false;
	}
}
