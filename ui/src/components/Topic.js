import React, { useState, useEffect }  from 'react';
import Fab from '@material-ui/core/Fab';
import Button from "@material-ui/core/Button";
import Slider from '@material-ui/core/Slider';
import TextField from "@material-ui/core/TextField";
import DialogContentText from "@material-ui/core/DialogContentText";
import Paper from '@material-ui/core/Paper';
import Dialog from "@material-ui/core/Dialog";
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import DialogActions from "@material-ui/core/DialogActions";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import InputLabel from '@material-ui/core/InputLabel';
import AddIcon from '@material-ui/icons/Add';
import Grid from '@material-ui/core/Grid';
import {useStyles} from '../App'
import Title from './Title'

const Topic = () => {
    const classes = useStyles();
    const [open, setOpen] = React.useState(false);

    const handleClickOpen = () => {
        setOpen(true);
      };

    const labelStyle = {
        paddingTop: '1em',
    };
    const replicationMarks = [
        {
          value: 1,
          label: '1',
        },
        {
          value: 2,
          label: '2',
        },
        {
          value: 3,
          label: '3',
        },
        {
          value: 5,
          label: '5',
        },
      ];
    
      const partitionMarks = [
        {
          value: 1,
          label: '1'
        },
        {
          value: 8,
          label: '8',
        },
        {
            value: 32,
            label: '32',
        },
        {
          value: 64,
          label: '64',
        },
        {
            value: 128,
            label: '128',
        },
        {
          value: 256,
          label: '256',
        },
      ];

    const [topics, setTopics] = useState([]);
    const loadTopics = () => {
      fetch('/v1/topics')
          .then(res => res.json())
          .then(
            (result) => {setTopics(result)},
            (error) => {console.log(error)}
          )
      }

    useEffect(loadTopics,[])
    
    const handleClose = () => {
        setOpen(false);
      };
    const [createTopic, updateCreateTopic] = React.useState([]);
    const handleCreateTopicChange = (e) => {
        updateCreateTopic({
          ...createTopic,
          [e.target.name]: e.target.value
        });
      };
    const handleCreateTopicSliderChange = (e) => {
        updateCreateTopic({
          ...createTopic,
          [e.target.parentElement.id]: parseInt(e.target.ariaValueNow)
        });
      };   
    const handleCreateTopic = () => {
      const item = {
        name: createTopic.name,
        partitionNumber: createTopic.partitionNumber || 1,
        replicationFactor: createTopic.replicationFactor || 1
      }
      fetch('/v1/topics', {
          method: 'POST',
          headers: { 
                    'Accept': 'application/json',
                    'Content-Type': 'application/json' 
                    },
          body: JSON.stringify(item)
        })
        .then(setOpen(false))
        .then(setTopics([...topics, item]))
        .catch((err) => { console.log(err); });
  }

    return (
        <Grid container spacing={3}>
            <Grid item xs={12}>
                <Paper className={classes.paper}>     
                    <Title>Topics</Title>    
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Partitions</TableCell>
                            <TableCell>Replication Factor</TableCell>
                            <TableCell>Under replicated</TableCell>
                            <TableCell align="right">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                        {topics && topics.map((topic) => (
                            <TableRow key={topic.name}>
                                <TableCell>{topic.name}</TableCell>
                                <TableCell>{topic.partitionNumber}</TableCell>
                                <TableCell>{topic.replicationFactor}</TableCell>
                                <TableCell>{topic.underReplicatedPartitions}</TableCell>
                                <TableCell align="right">N/A</TableCell>
                            </TableRow>
                            ))}
                        </TableBody>
                        </Table>
                </Paper>
            </Grid>

            <Dialog  open={open} aria-labelledby="form-dialog-title" onClose={handleClose}>
                <DialogTitle id="form-dialog-title">Create new topic</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Create a new data topic at the cluster managed by Kinesthesia.
                    </DialogContentText>
             
                    <TextField
                        autoFocus
                        margin="dense"
                        name="name"
                        id="name"
                        label="Topic name"
                        onChange={handleCreateTopicChange}
                        fullWidth
                    />
                    <InputLabel style={labelStyle}>Partitions</InputLabel>
                    
                    <Slider
                        defaultValue={1}
                        aria-labelledby="discrete-slider-restrict"
                        id="partitionNumber"
                        onChange={handleCreateTopicSliderChange}
                        step={1}
                        min={1}
                        max={256}
                        label="Relication Factor"
                        valueLabelDisplay="auto"
                        marks={partitionMarks}
                    /> 

                    <InputLabel style={labelStyle}>Replication Factor</InputLabel>
                       
                    <Slider
                        defaultValue={1}
                        aria-labelledby="discrete-slider-restrict"
                        step={null}
                        id="replicationFactor"
                        onChange={handleCreateTopicSliderChange}
                        min={1}
                        max={5}
                        label="Relication Factor"
                        valueLabelDisplay="auto"
                        marks={replicationMarks}
                    />
                </DialogContent>
                <DialogActions>
                    <Button  color="primary" onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button  color="primary" onClick={handleCreateTopic}>
                        Create
                    </Button>
                </DialogActions>
            </Dialog>

            <Fab color="primary" aria-label="add" className={classes.fab} onClick={handleClickOpen}>
                <AddIcon />
            </Fab>
        </Grid>)

}

export default Topic