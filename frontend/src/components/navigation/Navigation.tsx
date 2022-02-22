import { Menu } from 'antd';
import React from 'react';
import './Navigation.scss';

const Navigation: React.FC = () => {

    return (
        <>
            <Menu mode='horizontal' theme='dark'>
                <Menu.Item>Test
                </Menu.Item>
            </Menu>
        </>
    )
}

export default Navigation;