import { Button, message } from 'antd';
import Title from 'antd/lib/typography/Title';
import React from 'react';
import { ping } from '../../api/api';

const Overview: React.FC = () => {
    return (
        <div>
            <Button onClick={e => {
              ping().then(
                res => message.success(res)
              );
            }}>
              Ping backend
            </Button>
        </div>

    )
};

export default Overview;