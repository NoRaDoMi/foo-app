import {
	CloseCircleOutlined,
	DollarOutlined,
	ReloadOutlined,
	UndoOutlined,
	UserOutlined,
	WalletOutlined
} from '@ant-design/icons';
import { Button, Descriptions, Result, Typography } from 'antd';
import React from 'react';
import { useDispatch, useSelector } from 'react-redux';

const { Paragraph } = Typography;

function ResultStep(props) {
	const { selectedUser } = props;
	const responseCode = useSelector((state) => state.transferCodeResponse);
	const stepFormData = useSelector((state) => state.stepFormData);
	const balance = useSelector((state) => state.wallet.balance);
	const dispatch = useDispatch();
	const { amount } = stepFormData;

	let messageError = null;
	switch (responseCode) {
		case 1:
			messageError = 'Số dư hiện tại không đủ để thực hiện giao dịch. Thử lại !';
			break;
		case 2:
			messageError = 'Mật khẩu xác nhận không đúng. Thử lại !';
			break;
		default:
			break;
	}

	const onFinish = () => {
		dispatch({
			type: 'SAVE_CURRENT_STEP',
			data: { payload: 'info' }
		});
	};

	const onReconfirmPassword = () => {
		dispatch({
			type: 'SAVE_CURRENT_STEP',
			data: { payload: 'confirm' }
		});
	};

	const information = (
		<div>
			<Descriptions column={1}>
				<Descriptions.Item
					label={
						<span>
							<UserOutlined style={{ fontSize: '17px', marginRight: '5px', color: '#1890ff' }} /> Người
							nhận
						</span>
					}
				>
					{' '}
					<span style={{ fontWeight: 600 }}>{selectedUser.name}</span>
				</Descriptions.Item>

				<Descriptions.Item
					label={
						<span>
							<DollarOutlined style={{ fontSize: '17px', marginRight: '5px', color: '#1890ff' }} /> Số
							tiền chuyển
						</span>
					}
				>
					<span style={{ color: 'blue', fontWeight: 600 }}>
						{' '}
						{`${amount}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} VND
					</span>
				</Descriptions.Item>
				<Descriptions.Item
					label={
						<span>
							<WalletOutlined style={{ fontSize: '17px', marginRight: '5px', color: '#1890ff' }} /> Số dư
							còn lại
						</span>
					}
				>
					{' '}
					<span style={{ color: 'green', fontWeight: 600 }}>
						{' '}
						{`${balance}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} VND
					</span>
				</Descriptions.Item>
			</Descriptions>
		</div>
	);
	const extra = (
		<Button type="primary" onClick={onFinish} icon={<ReloadOutlined />}>
			Tiếp tục
		</Button>
	);

	const successResponse = () => {
		return (
			<Result status="success" title="Thành công" subTitle="" extra={extra}>
				{information}
			</Result>
		);
	};

	const serverIntervalErrorResponse = () => {
		return <Result status="500" title="500" subTitle="Lỗi hệ thống, vui lòng thử lại sau." extra={extra} />;
	};

	const submitFailedResponse = () => {
		return (
			<div style={{ marginTop: '50px' }}>
				<Result
					status="error"
					title="Giao dịch thất bại"
					subTitle={
						<Paragraph>
							<CloseCircleOutlined style={{ color: 'red' }} /> {messageError}
						</Paragraph>
					}
					extra={
						<Button
							type="primary"
							onClick={responseCode === 2 ? onReconfirmPassword : onFinish}
							icon={<UndoOutlined />}
						>
							Thực hiện lại
						</Button>
					}
				/>
			</div>
		);
	};

	return (
		<div>
			{responseCode === 0 ? (
				successResponse()
			) : responseCode === 4 ? (
				serverIntervalErrorResponse()
			) : (
				submitFailedResponse()
			)}
		</div>
	);
}

export default ResultStep;
