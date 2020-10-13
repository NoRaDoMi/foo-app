import { CommentOutlined, FireFilled, MessageFilled, WalletFilled, LogoutOutlined } from '@ant-design/icons';
import { Badge, Layout, Menu, Tooltip, Row, Col, Card, Dropdown } from 'antd';
import React from 'react';
import { connect, useDispatch, useSelector } from 'react-redux';
import { setActiveTabKeyAction, setHavingUnseenTransferAction } from '../../actions/fooAction';
import ConversationList from '../ConversationList';
import CustomAvatar from '../CustomAvatar';
import MessageList from '../MessageList';
import TransactionHistory from '../TransactionHistory';
import UserWallet from '../UserWallet';
import { GridContent } from '@ant-design/pro-layout';
import './Messenger.css';
import WallTransferList from '../WallTransferList';
import { hanldeLogout } from '../../services/logout';
import { useHistory } from 'react-router-dom';

const { Sider, Header, Content } = Layout;

function Messenger(props) {
	const activeTabKey = useSelector((state) => state.activeTabKey);
	const unseenChat = useSelector((state) => state.unseenChat);
	const unseenTransfer = useSelector((state) => state.unseenTransfer);
	console.log('Transfer status: ', unseenTransfer);
  const dispatch = useDispatch();
  const history = useHistory();

	const handleSideBarChange = (e) => {
    if(e.key === '3') {
      
      hanldeLogout().then(() => {
        console.log("Dang xuat");
        history.push('/login')});
      
    } else{
      dispatch(setActiveTabKeyAction(e.key));
      if (e.key === '2') dispatch(setHavingUnseenTransferAction(false));
    }
		
  };

	return (
		<div style={{ height: 100 + 'vh' }}>
			<Layout style={{ height: 100 + 'vh' }}>
				<Sider
					breakpoint="lg"
					collapsedWidth="0"
					onBreakpoint={(broken) => {}}
					onCollapse={(collapsed, type) => {}}
					width="75"
					id="main-side-menu"
				>
          <CustomAvatar type="user-avatar" style={{margin: "5px 0 0 5px"}} avatar={props.user.name || ''} />
					
					<div className="menu-separation" />
					<Menu
						theme="dark"
						id="side-menu"
						defaultSelectedKeys={[ '1' ]}
						mode="vertical"
						onSelect={handleSideBarChange}
					>
						<Menu.Item
							key="1"
							icon={
								<Tooltip placement="topLeft" title="Nhắn tin">
									{unseenChat ? (
										<Badge count={<FireFilled style={{ color: '#f5222d', fontSize: '15px' }} />}>
											<MessageFilled style={{ fontSize: 25 }} />
										</Badge>
									) : (
										<CommentOutlined style={{ fontSize: 25 }} />
									)}
								</Tooltip>
							}
						/>
						<Menu.Item
							key="2"
							icon={
								<Tooltip placement="topLeft" title="Ví">
									{unseenTransfer === 'true' ? (
										<Badge count={<FireFilled style={{ color: '#f5222d', fontSize: '15px' }} />}>
											<WalletFilled style={{ fontSize: 25 }} />
										</Badge>
									) : (
										<WalletFilled style={{ fontSize: 25 }} />
									)}
								</Tooltip>
							}
						/>
            <Menu.Item
							key="3"
							icon={
								<Tooltip placement="topLeft" title="Đăng xuất">
									<LogoutOutlined style={{fontSize: "25px"}}/>
								</Tooltip>
							}
						/>
					</Menu>
				</Sider>

				{activeTabKey !== '1' ? (
					<Layout>
						<Header className="site-layout-background" style={{ position: 'fixed', width: '100%' }} />
						<Content>
							{' '}
							<GridContent style={{ width: '100%' }}>
								<Row gutter={24}>
									<Col xl={7} lg={7} md={24} style={{ width: '100%' }}>
										<UserWallet />
										<WallTransferList />
									</Col>
									<Col xl={17} lg={17} md={24} style={{ width: '100%' }}>
										<TransactionHistory />
									</Col>
								</Row>
							</GridContent>
						</Content>
					</Layout>
				) : (
          <>
					<Sider
						breakpoint="lg"
						collapsedWidth="0"
						theme="light"
						onBreakpoint={(broken) => {}}
						onCollapse={(collapsed, type) => {}}
						width="350"
						id="sub-side-menu"
					>
						<ConversationList />
					</Sider>
          <MessageList/>
          </>
				)}
			</Layout>
		</div>
	);
}

let mapStateToProps = (state) => {
	return {
		user: state.user
	};
};

export default connect(mapStateToProps, null)(Messenger);

{
	/* <MessageList />
        
        /* <Sider
					breakpoint="lg"
					collapsedWidth="0"
					theme="light"
					onBreakpoint={(broken) => {}}
					onCollapse={(collapsed, type) => {}}
					width="350"
					id="sub-side-menu"
				>
					{activeTabKey === '1' ? <ConversationList /> : <UserWallet />}
				</Sider>
				{activeTabKey === '1' ? <MessageList /> : <TransactionHistory /> */
}
