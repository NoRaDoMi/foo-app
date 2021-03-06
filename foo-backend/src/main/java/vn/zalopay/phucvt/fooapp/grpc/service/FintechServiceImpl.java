package vn.zalopay.phucvt.fooapp.grpc.service;

import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import vn.zalopay.phucvt.fooapp.fintech.*;
import vn.zalopay.phucvt.fooapp.grpc.handler.fintech.GetBalanceHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.fintech.GetHistoryHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.fintech.TransferMoneyHandler;

@Log4j2
@Builder
public class FintechServiceImpl extends FintechServiceGrpc.FintechServiceImplBase {
  private final GetBalanceHandler getBalanceHandler;
  private final TransferMoneyHandler transferMoneyHandler;
  private final GetHistoryHandler getHistoryHandler;

  @Override
  public void getBalance(
      GetBalanceRequest request, StreamObserver<GetBalanceResponse> responseObserver) {
    getBalanceHandler.handle(request, responseObserver);
  }

  @Override
  public void transferMoney(
      TransferMoneyRequest request, StreamObserver<TransferMoneyResponse> responseObserver) {
    transferMoneyHandler.handle(request, responseObserver);
  }

  @Override
  public void getHistory(
      GetHistoryRequest request, StreamObserver<GetHistoryResponse> responseObserver) {
    getHistoryHandler.handle(request, responseObserver);
  }
}
