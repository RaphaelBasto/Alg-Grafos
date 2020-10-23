import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;

public class Digraph implements Serializable {
    protected HashMap<Integer,Vertex> vertex_set;
    protected int time;
    private Boolean acyclic;

    public Digraph() {
        vertex_set = new HashMap<Integer,Vertex>();
    }

    public void print() {
		System.out.print("\n\n -------------------------------");
        if( this.vertex_set.size() == 0 ) {
			System.out.printf("\n\n Conjunto de vértices vazio");
			System.out.print("\n\n -------------------------------");
			return;
		}
        System.out.printf("\n\n Grafo, grau máximo %d", this.max_degree());

        if( this.is_undirected() )
            System.out.println("\nNão direcionado");
        else
            System.out.println("\nDirecionado");

        for( Vertex v : vertex_set.values())
            v.print();
		System.out.print("\n\n -------------------------------");
    }
    
    public void add_vertex( ) {
		// fazer: utilizar um id disponível
	}

    public void add_vertex( int id ) {
        if ( this.vertex_set.get( id ) == null ) {
            Vertex v = new Vertex( id );
            vertex_set.put( v.id, v );
        }
    }
    
    public void add_arc( Integer id1, Integer id2) {
		try {
			Vertex v1 = vertex_set.get(id1);
			Vertex v2 = vertex_set.get(id2);
			v1.add_neighbor( v2 );
		} catch(Exception e) {
			this.add_vertex( id1 );
			this.add_vertex( id2 );
			Vertex v1 = vertex_set.get(id1);
			Vertex v2 = vertex_set.get(id2);
			v1.add_neighbor( v2 );
		}
    }
/*    public void add_arc( Integer id1, Integer id2) {
        Vertex v1 = vertex_set.get(id1);
        Vertex v2 = vertex_set.get(id2);
        v1.add_neighbor( v2 );
    }*/

	// implementação do add_arc sem try
    public void add_arc1( Integer id1, Integer id2) {
		this.add_vertex( id1 );
        Vertex v1 = vertex_set.get(id1);
		this.add_vertex( id2 );
        Vertex v2 = vertex_set.get(id2);
        v1.add_neighbor( v2 );
    }

    public void add_edge( Integer id1, Integer id2) {
		Vertex v1 = vertex_set.get(id1);
        Vertex v2 = vertex_set.get(id2);
        v1.add_neighbor( v2 );
        v2.add_neighbor( v1 );
    }

    public void del_vertex( int id ) {
        for( Vertex v1 : vertex_set.values()) {
			v1.nbhood.remove( id );
        }
        vertex_set.remove( id );
    }
    
    public void compact() {
		int n = vertex_set.size();
		int [ ] present = new int[n+1];
		Vertex [ ] stranges = new Vertex[n];
		for( int i = 1; i <= n; i++) {
			present[ i ] = 0;
		}
		int qst = 0;
        for( Vertex v1 : vertex_set.values() ) {
			if( v1.id <= n )
				present[ v1.id ] = 1;
			else
				stranges[ qst++ ] = v1;	
		}
		int i = 1;
		for( int pairs = 0; pairs < qst; i++ ) {
			if( present[ i ] == 0)		
				present[ pairs++ ] = i;
		}
		for( i = 0; i < qst; i++) {
			int old_id = stranges[ i ].id;
			stranges[ i ].id = present[ i ];
			for( Vertex v1 : vertex_set.values() ) {
				if( v1.nbhood.get( old_id ) != null ) {
					v1.nbhood.remove( old_id );
					v1.nbhood.put( stranges[ i ].id, stranges[ i ] );
				}
			}
		}
    }

    public int max_degree() {
        int max = -1;
        for( Vertex v1 : vertex_set.values() ) {
			if( v1.degree() > max )
				max = v1.degree();
		}
		return max;
    }

    public boolean is_undirected() {
        for( Vertex v1 : vertex_set.values()) {
            for( Vertex v2 : v1.nbhood.values()) {
                if (v2.nbhood.get(v1.id) == null)
                    return false;
            }
        }
        return true;
    }
    
    public Graph subjacent() {
        Graph g2 = new Graph( );
        for( Vertex v11 : this.vertex_set.values()) {
            g2.add_vertex( v11.id );
        }
        for( Vertex v11 : this.vertex_set.values()) {
            for( Vertex v12 : v11.nbhood.values()) {
                Vertex v21 = g2.vertex_set.get( v11.id );
                Vertex v22 = g2.vertex_set.get( v12.id );
                v21.add_neighbor( v22 );
                v22.add_neighbor( v21 );
            }
        }
        return g2;
    }
    public boolean is_connected() {
		// fazer: se direcionado ? encontrar o subjacente
        return false;
    }
    public int count_components() {
		// fazer
		return -1;
	}
    
    public void BFS( Integer id_raiz ) {
        Vertex raiz = vertex_set.get( id_raiz );
        raiz.d = 0;
        Queue<Vertex> lista = new LinkedList<Vertex>();
        lista.add( raiz );
        Vertex atual;
        while ((atual = lista.poll()) != null) {
            for( Vertex viz : atual.nbhood.values() ) {
                if( viz.d == null ) {
                    viz.discover( atual );
                    lista.add( viz );
                }
            }
        }
    }
    
    public void DFS( List<Vertex> ordering ) {
		if( ordering == null ) {
			ordering = new ArrayList<Vertex>( );
			ordering.addAll( vertex_set.values( ) );
		}
		acyclic = true;
		time = 0;
		for( Vertex v1 : ordering )
			if( v1.d == null )
				DFS_visit( v1 );
	}

    private void DFS_visit( Vertex v1 ) {
		v1.d = ++time;
		for( Vertex neig : v1.nbhood.values( ) ) {
			if( neig.d == null ) {
				neig.parent = v1;
				DFS_visit( neig );
			}
			else if (neig.d < v1.d)
				acyclic = false;
				// encontrar os vértices que formam esse ciclo
		}
		v1.f = ++time;
	}
	
	public List<Vertex> get_list_roots( ) {
		List<Vertex> list_roots = new ArrayList<Vertex>( );
		for( Vertex v1 : vertex_set.values( ) ) {
			if( v1.parent == null )
				list_roots.add( v1 );
		}
		return list_roots;
	}
	
	public boolean is_acyclic( ) {
		if( acyclic != null )
			return acyclic;
		DFS( null );
		return acyclic;
	}
	public List<Vertex> topological_sorting( ) {
		if( ! is_acyclic( ) ) {
			System.out.printf("\n\n O grafo contém ciclo!!");
			return null;
		}
		List<Vertex> ts_vertex_set = new ArrayList<Vertex>();
		for ( Vertex v1 : vertex_set.values( ) )
			ts_vertex_set.add( v1 );
		Collections.sort( ts_vertex_set );
		return ts_vertex_set;
	}
	public Digraph reverse( ) {
		Digraph d2 = new Digraph( );
		for( Vertex v11 : this.vertex_set.values( ) ) {
            d2.add_vertex( v11.id );
		} 
        for( Vertex v11 : this.vertex_set.values()) {
            for( Vertex v12 : v11.nbhood.values()) {
                Vertex v21 = d2.vertex_set.get( v11.id );
                Vertex v22 = d2.vertex_set.get( v12.id );
                v22.add_neighbor( v21 );
            }
        }
		return d2;
	}
	
	public void CFC( ) {
		DFS( null );
		
		List<Vertex> vertex_set1 = new ArrayList<Vertex>();
		for ( Vertex v1 : vertex_set.values( ) )
			vertex_set1.add( v1 );
		Collections.sort( vertex_set1 );
		
		Digraph d2 = this.reverse( );

		List<Vertex> vertex_set2 = new ArrayList<Vertex>();
		for ( Vertex v1 : vertex_set1 ) {
			Vertex v2 = d2.vertex_set.get( v1.id );
			vertex_set2.add( v2 );
		}
		
		d2.DFS( vertex_set2 );
		
		List<Vertex> list_roots = d2.get_list_roots( );
		
		for( Vertex v1 : d2.vertex_set.values( ) ) {
			v1.root = v1.get_root( );
			System.out.print( "\n v1 " + v1.id + " " + v1.root.id);
		}
		
		for( Vertex v1 : list_roots ) {
			System.out.print( "\n Outra CFC: " );
			for( Vertex v2 : d2.vertex_set.values( ) ) {
				if( v2.root == v1 )
					System.out.print(" " + v2.id );
			}
		}
	}
	
	public boolean is_bipartite( ) {
		Graph g1 = this.subjacent( );
		if( ! g1.is_bipartite( ) )
			return false;
		return true;
	}
}
