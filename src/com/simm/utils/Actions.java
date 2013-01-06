package com.simm.utils;


public class Actions {
	public static class StoreMinAndMax implements Action<Integer>{
		private int min = Integer.MAX_VALUE;
		private int max = Integer.MIN_VALUE;
		
		@Override
		public void action(Integer data) {
			min = Math.min(min, data);
			max = Math.max(max, data);
		}
		
		public int min(){
			return min;
		}
		
		public int max(){
			return max;
		}
	}
}
