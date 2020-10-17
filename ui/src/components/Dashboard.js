import React, { useState, useEffect }  from 'react';
import clsx from 'clsx';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Title from './Title'
import {useStyles} from '../App'

const Dasboard = () => {
    const classes = useStyles();
    const fixedHeightPaper = clsx(classes.paper, classes.fixedHeight);
    const [status, setStatus] = useState([]);
    useEffect(() => {
      fetch('/v1/status')
        .then(res => res.json())
        .then(
          (result) => {setStatus(result)},
          (error) => {console.log(error)}
        )
    },[])

    
    return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={6} lg={6}>
        <Paper className={fixedHeightPaper}>
          <Title>Cluster ID</Title>
            <Typography component="p">
              {status.clusterId}
            </Typography>
        </Paper>
      </Grid>
      <Grid item xs={12} md={6} lg={6}>
        <Paper className={fixedHeightPaper}>
        <Title>Node Count</Title>
            <Typography component="p">
              {status.nodeCount}
            </Typography>
        </Paper>
      </Grid>
      <Grid item xs={12}>
      <Paper className={classes.paper}>
        <Title>Cluster Nodes</Title>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Id</TableCell>
              <TableCell>Host</TableCell>
              <TableCell align="right">Rack</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>

            {status.nodes && status.nodes.map((node) => (
              <TableRow key={node.id}>
                <TableCell>{node.id}</TableCell>
                <TableCell>{node.host}:{node.port}</TableCell>
                <TableCell align="right">{node.rack}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Grid>
    </Grid>
  )
}

export default Dasboard