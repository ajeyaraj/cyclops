package com.aol.cyclops.internal.stream.operators;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.aol.cyclops.data.collections.extensions.standard.ListXImpl;
import com.aol.cyclops.util.stream.StreamUtils;


public class BatchByTimeAndSizeOperator<T, C extends Collection<? super T>> {
    private final Stream<T> stream;
    private final Supplier<C> factory;
	public BatchByTimeAndSizeOperator(Stream<T> stream){
		this.stream = stream;
		factory = ()-> (C)new ListXImpl<>();
	}
	public BatchByTimeAndSizeOperator(Stream<T> stream2, Supplier<C> factory2) {
		this.stream=stream2;
		this.factory=factory2;
	}
	public  Stream<C> batchBySizeAndTime(int size, long time, TimeUnit t){
		Iterator<T> it = stream.iterator();
		long toRun = t.toNanos(time);
		return StreamUtils.stream(new Iterator<C>(){
			long start = System.nanoTime();
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			@Override
			public C next() {
				
				
				C list = factory.get();
				while(System.nanoTime()-start< toRun && it.hasNext() && list.size()<size){
					list.add(it.next());
				}
				start = System.nanoTime();
				return list;
			}
			
		}).filter(l->l.size()>0);
	}
}
