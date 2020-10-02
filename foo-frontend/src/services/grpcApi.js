import { FintechServiceClient } from '../grpc/fintech_grpc_web_pb.js';
import { GetBalanceRequest, TransferMoneyRequest, GetHistoryRequest } from '../grpc/fintech_pb.js';
import { getJwtFromStorage } from '../utils/utils.js';

const URL = 'http://' + window.location.hostname + ':8080';

const client = new FintechServiceClient(URL, null, null);

export default {
	getBalance: (name, callback) => {
		const getBalanceRequest = new GetBalanceRequest();
		client.getBalance(getBalanceRequest, prepareMetadata(), callback);
	},

	transferMoney: (request, callback) => {
		const transferMoneyRequest = new TransferMoneyRequest();
		transferMoneyRequest.setReceiver(request.receiver);
		transferMoneyRequest.setAmount(request.amount);
		transferMoneyRequest.setDescription(request.description);
		transferMoneyRequest.setConfirmPassword(request.confirmPassword);
		client.transferMoney(transferMoneyRequest, prepareMetadata(), callback);
	},

	getHistory: (callback) => {
		console.log('Get history');
		const getHistoryRequest = new GetHistoryRequest();
		getHistoryRequest.setPageSize(10);
		getHistoryRequest.setPageToken(10);
		client.getHistory(getHistoryRequest, prepareMetadata(), callback);
	}
};

function prepareMetadata() {
	const jwtToken = getJwtFromStorage();
	const metadata = { jwt: jwtToken };
	return metadata;
}
