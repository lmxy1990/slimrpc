package github.slimrpc.core.event;

import github.slimrpc.core.constant.ClientDaemonThreadEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class TimerAndEventDaemonThread extends Thread {
	static Logger log = LoggerFactory.getLogger(TimerAndEventDaemonThread.class);

	private volatile boolean toStop = false;
	private long intervalMs = 1000;
	private final BlockingQueue<ClientDaemonThreadEvent> blockingQueue;
	private final List<Runnable> timerJobList = new CopyOnWriteArrayList<Runnable>();
	private final Map<Byte, Runnable> eventHandlerMap = new ConcurrentHashMap<Byte, Runnable>();

	public TimerAndEventDaemonThread(long intervalMs, BlockingQueue<ClientDaemonThreadEvent> blockingQueue) {
		super("TimerAndEventDaemonThread");
		this.intervalMs = intervalMs;
		setDaemon(true);
		this.blockingQueue = blockingQueue;
	}

	public void close() {
		toStop = true;
		blockingQueue.offer(new ClientDaemonThreadEvent(ClientDaemonThreadEventType.closeDaemonThread));
	}

	@Override
	public void run() {
		while (true) {
			if (toStop) {
				return;
			}

			if (isInterrupted()) {
				return;
			}

			long startTime = System.currentTimeMillis();
			for (Runnable job : timerJobList) {
				try {
					job.run();
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					continue;
				}
			}
			long elapsedTime = System.currentTimeMillis() - startTime;
			if (elapsedTime > intervalMs) {
				elapsedTime = intervalMs;
			}

			try {
				ClientDaemonThreadEvent event = blockingQueue.poll(intervalMs + 1 - elapsedTime, TimeUnit.MILLISECONDS);

				if (event != null) {
					log.info("{eventType:" + event.getEventType() + "}");
					Runnable job = eventHandlerMap.get(event.getEventType());
					if (job != null) {
						job.run();
					} else {
						log.error("{msg:\"eventHandlerMap no event handler\", eventType:" + event.getEventType() + "}");
					}

				}
			} catch (InterruptedException ex) {
				break;
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				continue;
			}
		}
	}

	public TimerAndEventDaemonThread addTimerJob(Runnable timerJob) {
		this.timerJobList.add(timerJob);
		return this;
	}

	public TimerAndEventDaemonThread clearTimerJobList() {
		timerJobList.clear();
		return this;
	}

	public TimerAndEventDaemonThread addEventHanler(byte eventType, Runnable eventHanler) {
		eventHandlerMap.put(eventType, eventHanler);
		return this;
	}

}
