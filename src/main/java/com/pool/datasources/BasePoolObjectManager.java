package com.pool.datasources;


public abstract class BasePoolObjectManager<T> {

	static class IdentityWrapper<T>{
		T instances;
		public IdentityWrapper(T instances){
			this.instances = instances;
		}
		@Override
		public int hashCode() {
			return System.identityHashCode(instances);
		}
		@SuppressWarnings("rawtypes")
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return ((IdentityWrapper)obj).instances == instances;
		}
		
	}
}
