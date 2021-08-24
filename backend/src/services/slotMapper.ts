import {Request, Response, NextFunction} from 'express';
import logging from '../config/logging';

const NAMESPACE = "Mapper";

const timetable= [[{1:"A1",2:"L1"},{1:"F1",2:"L2"},{1:"D1",2:"L3"},
                {1:"TB1",2:"L4"},{1:"TG1",2:"L5"},{1:"-",2:"L6"},
                {1:"A2",2:"L31"},{1:"F2",2:"L32"},{1:"D2",2:"L33"},
                {1:"TB2",2:"L34"},{1:"TG2",2:"L35"},{1:"-",2:"L36"},
                {1:"V3",2:"-"}],
                [{1:"B1",2:"L7"},{1:"G1",2:"L8"},{1:"E1",2:"L9"},
                {1:"TC1",2:"L10"},{1:"TAA1",2:"L11"},{1:"-",2:"L12"},
                {1:"B2",2:"L37"},{1:"G2",2:"L38"},{1:"E2",2:"L39"},
                {1:"TC2",2:"L40"},{1:"TAA2",2:"L41"},{1:"-",2:"L42"},
                {1:"V4",2:"-"}],
                [{1:"C1",2:"L13"},{1:"A1",2:"L14"},{1:"F1",2:"L15"},
                {1:"V1",2:"L16"},{1:"V2",2:"L17"},{1:"-",2:"L18"},
                {1:"C2",2:"L43"},{1:"A2",2:"L44"},{1:"F2",2:"L45"},
                {1:"TD2",2:"L46"},{1:"TBB2",2:"L47"},{1:"-",2:"L48"},
                {1:"V5",2:"-"}],
                [{1:"D1",2:"L19"},{1:"B1",2:"L20"},{1:"G1",2:"L21"},
                {1:"TE1",2:"L22"},{1:"TCC1",2:"L23"},{1:"-",2:"L24"},
                {1:"D2",2:"L49"},{1:"B2",2:"L50"},{1:"G2",2:"L51"},
                {1:"TE2",2:"L52"},{1:"TCC2",2:"L53"},{1:"-",2:"L54"},
                {1:"V6",2:"-"}],
                [{1:"E1",2:"L25"},{1:"C1",2:"L26"},{1:"TA1",2:"L27"},
                {1:"TF1",2:"L28"},{1:"TD1",2:"L29"},{1:"-",2:"L30"},
                {1:"E2",2:"L55"},{1:"C2",2:"L56"},{1:"TA2",2:"L57"},
                {1:"TF2",2:"L58"},{1:"TDD2",2:"L59"},{1:"-",2:"L60"},
                {1:"V7",2:"-"}],
                [{1:"V8",2:"L71"},{1:"X11",2:"L72"},{1:"X12",2:"L73"},
                {1:"Y11",2:"L74"},{1:"Y12",2:"L75"},{1:"-",2:"L76"},
                {1:"X21",2:"L77"},{1:"Z21",2:"L78"},{1:"Y21",2:"L79"},
                {1:"W21",2:"L80"},{1:"W22",2:"L81"},{1:"-",2:"L82"},
                {1:"V9",2:"-"}],
                [{1:"V10",2:"L83"},{1:"Y11",2:"L84"},{1:"Y12",2:"L85"},
                {1:"X11",2:"L86"},{1:"X12",2:"L87"},{1:"-",2:"L88"},
                {1:"Y21",2:"L89"},{1:"Z21",2:"L90"},{1:"X21",2:"L91"},
                {1:"W21",2:"L92"},{1:"W22",2:"L93"},{1:"-",2:"L94"},
                {1:"V11",2:"-"}]]


const slotMapper = (slotArr: any, req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Mapping all the slots`);
    let slots = [
        [{name:"A1/L1", free:false},{name:"F1/L2" , free:false},{name:"D1/L3", free:false},{name:"TB1/L4", free:false},{name:"TG1/L5", free:false},{name:"L6", free:false},{name:"LUNCH", free: false},{name:"A2/L31", free:false},{name:"F2/L32", free:false},{name:"D2/L33", free:false},{name:"TB2/L34", free:false},{name:"TG2/L35", free:false},{name:"L36", free:false},{name:"V3", free:false}],
        [{name:"B1/L7", free:false},{name:"G1/L8", free:false},{name:"E1/L9", free:false},{name:"TC1/L10", free:false},{name:"TAA1/L11", free:false},{name:"L12", free:false},{name:"LUNCH", free: false},{name:"B2/L37", free:false},{name:"G2/L38", free:false},{name:"E2/L39", free:false},{name:"TC2/L40", free:false},{name:"TAA2/L41", free:false},{name:"L42", free:false},{name:"V4", free:false}],
        [{name:"C1/L13", free:false},{name:"A1/L14", free:false},{name:"F1/L15", free:false},{name:"V1/L16", free:false},{name:"V2/L17", free:false},{name:"L18", free:false},{name:"LUNCH", free: false},{name:"C2/L43", free:false},{name:"A2/L44", free:false},{name:"F2/L45", free:false},{name:"TD2/L46", free:false},{name:"TBB2/L47", free:false},{name:"L48", free:false},{name:"V5", free:false}],
        [{name:"D1/L19", free:false},{name:"B1/L20", free:false},{name:"G1/L21", free:false},{name:"TE1/L22", free:false},{name:"TCC1/L23", free:false},{name:"L24", free:false},{name:"LUNCH", free: false},{name:"D2/L49", free:false},{name:"B2/L50", free:false},{name:"G2/L51", free:false},{name:"TE2/L52", free:false},{name:"TCC2/L53", free:false},{name:"L54", free:false},{name:"V6", free:false}],
        [{name:"E1/L25", free:false},{name:"C1/L26", free:false},{name:"TA1/L27", free:false},{name:"TF1/L28", free:false},{name:"TD1/L29", free:false},{name:"L30", free:false},{name:"LUNCH", free: false},{name:"E2/L55", free:false},{name:"C2/L56", free:false},{name:"TA2/57", free:false},{name:"TF2/L58", free:false},{name:"TDD2/59", free:false},{name:"L60", free:false},{name:"V7", free:false}],
        [{name:"V8/L71", free:false},{name:"X11/L72", free:false},{name:"X12/L73", free:false},{name:"Y11/L74", free:false},{name:"Y12/L75", free:false},{name:"L76", free:false},{name:"LUNCH", free: false},{name:"X21/L77", free:false},{name:"Z21/L78", free:false},{name:"Y21/L79", free:false},{name:"W21/L80", free:false},{name:"W22/L81", free:false},{name:"L82", free:false},{name:"V9", free:false}],
        [{name:"V10/L83", free:false},{name:"Y11/L84", free:false},{name:"Y12/L85", free:false},{name:"X11/L86", free:false},{name:"X12/L87", free:false},{name:"L88", free:false},{name:"LUNCH", free: false},{name:"Y21/L89", free:false},{name:"Z21/L90", free:false},{name:"X21/L91", free:false},{name:"W21/L92", free:false},{name:"W22/L93", free:false},{name:"L94", free:false},{name:"V11", free:false}]
    ]
    slotArr.forEach((slot:string) =>{
        for(let i=0;i<timetable.length;i++){
            let flag = 0;
            for(let j=0;j<timetable[i].length;j++){
                if(slot==timetable[i][j][1] && timetable[i][j][2]!="-"){
                    slots[i][j]["free"] = true;
                    if(!slotArr.includes(timetable[i][j][2])){
                        slots[i][j]["free"] = false;
                    }
                    flag = 1;
                    break;
                }
                else if(slot==timetable[i][j][2] && timetable[i][j][1]!="-"){
                    slots[i][j]["free"] = true;
                    if(!slotArr.includes(timetable[i][j][1])){
                        slots[i][j]["free"] = false;
                    }
                    flag = 1;
                    break;
                }
                else if(slot==timetable[i][j][1] && timetable[i][j][2]=="-"){
                    slots[i][j]["free"] = true;
                    flag = 1;
                    break;
                }
                else if(slot==timetable[i][j][2] && timetable[i][j][1]=="-"){
                    slots[i][j]["free"] = true;
                    flag = 1;
                    break;
                }
            }
            if(flag==1){
                break;
            }
        }
    })
    return res.status(200).send({"Slots": slots});
}

export default slotMapper;