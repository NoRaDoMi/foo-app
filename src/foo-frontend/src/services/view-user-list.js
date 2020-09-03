import { api } from './api';
import { updateCurrSessionId, fetchUserList } from '../redux/fooAction';
import store from '../redux/fooStore';

export function getUserList() {
	return new Promise((resolve, reject) => {
		api
			.authGet('/api/protected/userlist', null)
			.then((response) => {
				
				let items = response.data.data.items;
				console.log('Get User list successfully with size: ',items.length);
				let result = [];

				items.forEach((item) => {
					var userItem = {
						userId: item.userId,
						name: item.fullname,
						avatar: processUsernameForAvatar(item.fullname),
						online: item.online
					};
					result.push(userItem);
				});

				if (result.length > 0) {
					// Sap xep danh sach user theo tu tu dien theo ten.
					result.sort(function(a, b) {
						if (a.name < b.name) return -1;
						if (a.name > b.name) return 1;
						return 0;
					});

					store.dispatch(fetchUserList(result));
					store.dispatch(updateCurrSessionId(result[0].userId));
				}

				resolve();
			})
			.catch((reason) => {
				console.log(reason);
				reject('Fetch user list failed');
			});
	});
}

function processUsernameForAvatar(username) {
	var x1 = username.charAt(0);
	var x2 = username.charAt(1);
	return x1 + ' ' + x2;
}