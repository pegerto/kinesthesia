import React from 'react';
import { useHistory } from 'react-router-dom';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import DashboardIcon from '@material-ui/icons/Dashboard';
import StorageIcon from '@material-ui/icons/Storage';


export default function BarMenu() {
    const history = useHistory()
    return (
        <div>
            <List>
                <ListItem button onClick={() => history.push('/')} >
                    <ListItemIcon>
                        <DashboardIcon />
                    </ListItemIcon>
                    <ListItemText primary="Dashboards" />
                </ListItem>
                <ListItem button onClick={() => history.push('/topics')}>
                    <ListItemIcon>
                        <StorageIcon />
                    </ListItemIcon>
                    <ListItemText primary="Topics" />
                </ListItem>
            </List>
        </div>
    )
}