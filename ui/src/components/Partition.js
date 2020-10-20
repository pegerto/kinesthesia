import React, { useState, useEffect }  from 'react'
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Grid from '@material-ui/core/Grid';
import {useStyles} from '../App'
import Title from './Title'
import CheckBoxOutlineBlankIcon from '@material-ui/icons/CheckBoxOutlineBlank';
import { Tooltip } from '@material-ui/core';

const PartitionIcon = (props) => {
    const tp = props.topic + "-" + props.partition
    const color = props.underReplicated? 
        'error': props.leader?'primary':'disabled'

    return (
    <Tooltip title={tp}>
        <CheckBoxOutlineBlankIcon 
            color={color}
        />
    </Tooltip>
    )
}


const Partition = () => {
    const classes = useStyles();
    const [partitions, setPartitions] = useState(false);

    useEffect(()=> {
        fetch('/v1/partitions')
            .then(res => res.json())
            .then(
                (result) => {setPartitions(result)},
                (error) => {console.log(error)}
            )
    },[])

    return (
        <Grid container spacing={3}>
            <Grid item xs={12}>
                <Paper className={classes.paper}>     
                    <Title>Partition Grid</Title>    
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                            <TableCell size="small"></TableCell>
                            <TableCell>Partitions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                        {partitions && Object.keys(partitions).map((node, _) => (
                            <TableRow key={node}>
                                <TableCell size="small">{node}</TableCell>
                                <TableCell>
                                    {partitions[node].map(p => <PartitionIcon
                                        topic={p.topic}
                                        partition={p.partitionNumber}
                                        underReplicated={p.underReplicated}
                                        leader={p.leader}
                                    />)}
                                   
                                </TableCell>
                            </TableRow>
                            ))}
                        </TableBody>
                        </Table>
                </Paper>
            </Grid>
        </Grid>
    )
}


export default Partition