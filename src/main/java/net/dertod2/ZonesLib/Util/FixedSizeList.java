package net.dertod2.ZonesLib.Util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An ArrayList implementation with fixed size.<br />
 * When new elements will be added and the size is higher than maxSize than the oldest element will be removed
 * @author DerTod2
 *
 * @param <E>
 */
public class FixedSizeList<E> extends ArrayList<E> {
	private static final long serialVersionUID = -5962342969008334984L;
	private int maxSize;
	
	public FixedSizeList(int maxSize) {
		super(maxSize);

		if (maxSize <= 0) throw new RuntimeException("The maxSize can't be lower than zero.");
		this.maxSize = maxSize;
	}
	
	public boolean add(E e) {
		while (this.size() >= this.maxSize) {
			this.remove(0);
		}
		
		return super.add(e);
	}
	
	public boolean addAll(Collection<? extends E> c) {
		while (this.size() >= (this.maxSize + c.size())) {
			this.remove(0);
		}
		
		return super.addAll(c);
	}
	
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}
	
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
}