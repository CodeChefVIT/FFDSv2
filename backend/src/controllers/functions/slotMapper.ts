import logging from '../../config/logging';

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

const slots = [
    [{name:"A1/L1"},{name:"F1/L2"},{name:"D1/L3", free:false},{name:"TB1/L4", free:false},{name:"TG1/L5", free:false},{name:"L6", free:false},{name:"A2", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}],
    [{name:"A1/L1"},{name:"F1/L2"},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}],
    [{name:"A1/L1"},{name:"F1/L2"},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}],
    [{name:"A1/L1"},{name:"F1/L2"},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}],
    [{name:"A1/L1"},{name:"F1/L2"},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}],
    [{name:"A1/L1"},{name:"F1/L2"},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}],
    [{name:"A1/L1"},{name:"F1/L2"},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false},{name:"", free:false}]
]


const slotMapper = (slots: Array<string>) =>{
    logging.info(NAMESPACE,`Mapping all the slots`);

    slots.forEach((s)=>{
        
    })
    
}

export default slotMapper;