/*
 * Copyright (C) 2013-2018 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.kraken.dstarlite;

import pfg.kraken.dstarlite.navmesh.NavmeshNode;

/**
 * Un nœud du D* Lite.
 * 
 * @author pf
 *
 */

public final class DStarLiteNode
{
	public final NavmeshNode node;
	public int bestVoisin;
	public final Cle cle = new Cle();
	public int g = Integer.MAX_VALUE, rhs = Integer.MAX_VALUE;
	public Double heuristiqueOrientation = null;
	public int indexPriorityQueue;

	/**
	 * "done" correspond à l'appartenance à U dans l'algo du DStarLite
	 */
	public boolean inOpenSet = false;
	public long nbPF = 0;

	public DStarLiteNode(NavmeshNode gridpoint)
	{
		this.node = gridpoint;
	}

	public final boolean isConsistent()
	{
		return rhs == g;
	}
	
	@Override
	public final int hashCode()
	{
		return node.nb;
	}

	@Override
	public final boolean equals(Object o)
	{
		return o != null && node.nb == o.hashCode();
	}

	@Override
	public String toString()
	{
		return node + " (" + cle + "), inOpenSet : "+inOpenSet+", rhs = "+rhs+", g = "+g;
	}

	/**
	 * Initialisation du nœud s'il n'a pas encore été utilisé pour ce
	 * pathfinding
	 * 
	 * @param nbPF
	 */
	public final void update(long nbPF)
	{
		if(this.nbPF != nbPF)
		{
			g = Integer.MAX_VALUE;
			rhs = Integer.MAX_VALUE;
			inOpenSet = false;
			heuristiqueOrientation = null;
			this.nbPF = nbPF;
		}
	}

}