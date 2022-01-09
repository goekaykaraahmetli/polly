package com.polly.utils.communication;

abstract class DataStreamHandler extends Thread{
	private boolean running = false;
	protected final DataStreamManager dataStreamManager;
	
	protected DataStreamHandler(DataStreamManager dataStreamManager) {
		this.dataStreamManager = dataStreamManager;
	}


	@Override
	public void run() {
		while(running) {
			handleDataStream();
		}
	}
	
	@Override
	public synchronized void start() {
		running = true;
		super.start();
	}
	
	public void stopHandler() {
		running = false;
	}
	
	protected abstract void handleDataStream();
}
