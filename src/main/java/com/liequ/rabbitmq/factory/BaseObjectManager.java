package com.liequ.rabbitmq.factory;


public abstract class BaseObjectManager<T> {

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
			return ((IdentityWrapper)obj).instances == instances;
		}
		
	}
}
