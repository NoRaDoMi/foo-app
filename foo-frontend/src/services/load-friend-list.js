import grpcApi from './grpcApi';
import { loadFriendListAction, setSelectedUserAction } from '../actions/fooAction';
import store from '../store/fooStore';

export function getFriendList() {
	return new Promise((resolve, reject) => {
		grpcApi.getFriendList((err, response) => {
			const dataResponse = response;
			const items = dataResponse.getData().getItemsList();
			console.log('Items friend list: ' + items);
			let result = [];
			items.forEach((item) => {
				let userItem = {
					userId: item.getUserId(),
					name: item.getName(),
					avatar: processUsernameForAvatar(item.getName()),
					unreadMessages: item.getUnreadMessages(),
					lastMessage: item.getLastMessage(),
					online: item.getIsOnline()
				};
				result.push(userItem);
			});
			if (result.length > 0) {
				// Sap xep danh sach friends theo tu tu dien theo ten.
				result.sort(function(a, b) {
					if (a.name < b.name) return -1;
					if (a.name > b.name) return 1;
					return 0;
				});

				store.dispatch(loadFriendListAction(result));
				const initSelectedUser = {
					id: result[0].userId,
					name: result[0].name,
					avatar: result[0].avatar
				};
				store.dispatch(setSelectedUserAction(initSelectedUser));
			}
			resolve(result);
		});
		// api
		// 	.authGet('/api/protected/users', null)
		// 	.then((response) => {
		// 		let items = response.data.data.items;
		// 		let result = [];
		// 		items.forEach((item) => {
		// 			var userItem = {
		// 				userId: item.userId,
		// 				name: item.name,
		// 				avatar: processUsernameForAvatar(item.name),
		// 				online: item.online
		// 			};
		// 			result.push(userItem);
		// 		});

		// 		if (result.length > 0) {
		// 			// Sap xep danh sach user theo tu tu dien theo ten.
		// 			result.sort(function(a, b) {
		// 				if (a.name < b.name) return -1;
		// 				if (a.name > b.name) return 1;
		// 				return 0;
		// 			});

		// 			store.dispatch(loadUserListAction(result));
		// 			const initSelectedUser = {
		// 				id: result[0].userId,
		// 				name: result[0].name,
		// 				avatar: result[0].avatar
		// 			};
		// 			store.dispatch(setSelectedUserAction(initSelectedUser));
		// 		}

		// 		resolve(result);
		// 	})
		// .catch((reason) => {
		// 	console.log(reason);
		// 	reject('Fetch user list failed');
		// });
	});
}

function processUsernameForAvatar(username) {
	var x1 = username.charAt(0);
	var x2 = username.charAt(1);
	return x1 + ' ' + x2;
}