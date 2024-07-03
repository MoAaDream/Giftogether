package com.moadream.giftogether.funding.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.moadream.giftogether.funding.model.FundingRequest;

@Component
public class FundingQueueManager {
	private ConcurrentLinkedQueue<FundingRequest> queue = new ConcurrentLinkedQueue<>();
	private final FundingService fundingService;

	@Autowired
	public FundingQueueManager(@Lazy FundingService fundingService) {
		this.fundingService = fundingService;
		// 별도의 스레드에서 큐를 지속적으로 모니터링하고 처리
		new Thread(this::processQueue).start();
	}

	private void processQueue() {
		while (true) {
			FundingRequest request = queue.poll();
			if (request != null) {
				fundingService.processFundingRequest(request);

			}
			try {
				Thread.sleep(100); // 큐가 비어있으면 100ms 대기
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return; // 스레드가 중단되면 처리를 중지합니다.
			}
		}
	}

	public CompletableFuture<FundingRequest> addFundingRequest(FundingRequest request) {
		CompletableFuture<FundingRequest> future = new CompletableFuture<>();
		queue.add(request);
		// 아이템을 처리하면서 future를 완료할 수 있는 방식으로 큐를 처리한다고 가정
		processQueueWithCompletion(future);
		return future;
	}

	private void processQueueWithCompletion(CompletableFuture<FundingRequest> future) {
		while (!queue.isEmpty()) {
			FundingRequest request = queue.poll();
			if (request != null) {
				fundingService.processFundingRequest(request);
				future.complete(request); // Mark the future as complete when done
			}
		}
	}

}
