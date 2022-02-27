import { Button, Menu } from 'antd';
import React, { useState } from 'react';
import './Navigation.scss';
import { UserOutlined } from '@ant-design/icons';
import Login from '../login/Login';

const Navigation: React.FC = () => {

    const [isLoginModalVisible, setIsLoginModalVisible] = useState(false);

    function onLoginClick() {
        setIsLoginModalVisible(true);
    }

    return (
        <>
            <Menu mode='horizontal' theme='dark'>
                <Menu.Item>Test</Menu.Item>
                <li className='navigation-login'>
                    <div>
                        <Button onClick={e => onLoginClick()}>
                            <UserOutlined />
                            Login
                        </Button>
                        <Login 
                        isVisible={isLoginModalVisible}
                        onCancel={() => setIsLoginModalVisible(false)}
                        onSuccess={() => setIsLoginModalVisible(false)}/>
                    </div>
                </li>
            </Menu>
        </>
    )
}

export default Navigation;