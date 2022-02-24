import { Button, Menu } from 'antd';
import React from 'react';
import './Navigation.scss';
import { UserOutlined } from '@ant-design/icons';

const Navigation: React.FC = () => {

    return (
        <>
            <Menu mode='horizontal' theme='dark'>
                <Menu.Item>Test</Menu.Item>
                <li className='navigation-login'>
                    <div>
                        <Button>
                            <UserOutlined/>
                            Login
                        </Button>
                    </div>
                </li>
            </Menu>
        </>
    )
}

export default Navigation;