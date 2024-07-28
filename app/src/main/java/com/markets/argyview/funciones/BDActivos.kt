package com.markets.argyview.funciones

import android.util.Log

class BDActivos {
    companion object{
        val bonos = listOf("AL35","BPA7D","TX28","AL30","PMM29","TZV25","BPC7D","TDG24","BPJ5D","TVPP","DICPD","BA7DD","NDT5D","TX26","BPD7C","AL41D","AL35D","AL29","GD46D","NDT25","S24DD","AL30D","CO26","TZXM5","CUAP","T5X4","GD30D","PARPY","GD38","TZX27","BPOC7","BPOB7","BPY6C","BB7DD","GD29","TZXD7","GD35Y","SA24D","BPY26","BPOA7","TZX25","TZX26","GD41","GD30C","GD35C","TVPY","T2X5Y","TDE25","TVPA","TO26D","TX26D","GD29Y","TDJ4D","AL30C","TX31","BPB7D","TX28D","TX25D","T2X4Y","DIP0","GD38Y","AL41","BPA7C","GD29D","GD35","TDJ4Y","PAP0","CO26D","AL29D","DICPY","BPD7D","PARP","AL30Y","DICP","BPOD7","GD30","T4X4D","PM29D","T4X4","BA37D","T2X4","BPY6D","T2V4","AE38D","BPJ5C","T2X5","BPJ25","TVPE","TDJ24","BDC28","BB37D","TXM5Y","GD38C","TV25","BC37D","TO26","TZXD6","PARPD","GD41D","T4X4Y","GD41C","AE38","TX25","PBA25","BPC7C","CUAPD","GD46","BPJ5Y","TZX28","GD35D","TZXD5","GD38D","GD30Y","TVPAD","TC25P","BPB7C","GD41Y","TZXM6")
        val ONs = listOf("IRCID","PNWCO","MRCOO","YCA6P","AEC1O","IRCLD","MRCHO","RUC5D","IRCFO","MRCAO","GN40O","MGCLD","CAC8O","TTC7D","IRCFD","NBS1D","OTS2O","DNC2D","DNC3D","CSKZO","LOC3O","IRCLO","YMCQO","IRCJD","MRCID","MTCGO","MGCGO","LECAO","YMCVD","DNC4O","YMCVY","LECAD","YMCOO","AEC1D","CS38O","RUC5O","RUC7O","MRCYD","MRCIO","LECBO","LECDO","MGCED","LMS8D","CAC5D","CS37O","CS34D","CP31O","RUCAO","LOC2D","RAC6D","MGCEO","YMCHO","LECBD","YCA6O","PNMCO","CRCED","MRCYO","MGCJO","YMCHD","MSSAD","MGC9D","CP17D","YMCIO","CP30O","CLSIO","MRCRO","MRCPO","MRCQO","MSSEO","MSSAO","VSCEO","YMCJD","YFCDO","VSCFO","HBC4O","CS44D","CS43O","CP34D","MRCLD","MRCGO","LECEO","GNCXO","VSCGO","NBS1O","CS44O","MGC9O","MRCQD","RFCAC","YMCJO","YMCOD","MSSED","LOC2O","MRCLO","ARC1O","VSCED","MGCHO","SNS8O","SNS9D","ARC1D","MGCHD","CP30D","FOS2O","TLC5O","PN5CO","RCCJO","LECFO","MRCFO","CP31D","TLC1D","LMS8O","RUC8O","LOC3D","LMS7O","PNDCD","RCCJC","NPCAO","TLCDO","LMS9O","RUCAD","RUC6O","RUC6D","MGCLO","GNCXD","TLC5D","BOL1O","CLSID","CP32O","YMCVO","CAC5O","VSCLO","TTC7O","YMCID","GN40D","IRCJO","RFCAO","RCCMO","PECBD","MTCGD","CRCJO","TLC1O","OLC4O","PNTCO","IRCGO","VSCPO","MGCJD","IRCHO","CRCEO","RCCJD","IRCIO","CRCJD","YMCMO","DNC2O","IRCHD","OTS2D","PNTCD","HJCBO","PN7CO","RAC6O","MRCAD","GNCXY","VSCOO","YMCHC","GN43D","RFCAD","BOL1D","MROCD","GN43O","PNXCD","VSC6D","IRCGD","CS37D","YCAMO","CP17O","VSCPD","CAC8D","DNC3O","DNC1O","PECBO","GN34O","MRCUO","SNS9O","CS38D","YFCGO","VSCHO","LUC4O","VSCOD","TLCHO","MRFCD","YMCQD","RCCMD","CS34O","NPCBO","PNVCD","MRCKO","LMS7D","PNXCO","IRCKO","LECED","RUC7D","CP32D","MRCMO","TLC5C","NPCAD","DNC1D","NPCBD","CP34O","VSCFD","VSCGD")
        val acciones = listOf("YPFD","COME","TECO2","BMA","IRSA","SUPV","TGNO4","TXAR","CEPU","BYMA","TRAN","BBAR","LOMA","VALO","TGSU2","MIRG","CRES","GGAL","EDN","PAMP","ALUA")
        val accionesGeneral = listOf("BBARB","CAPX","FIPL","HARGB","CECO2","YPFDB","HAVA","CRE3W","TECOB","PAMPB","HSAT","SUPVD","PAMPD","LOMAB","CEPUB","GCDI","CTIO","CVH","CECOB","BYMAD","SEMI","TGSUB","BOLT","DGCE","VALOC","IRS2W","YPFDD","SUPVB","MTR","GAMI","GGALB","ROSE","EDND","LONG","BHIPB","MOLA","HARG","OEST","GGADB","REGE","BHIP","BMA.D","CEPDB","GARO","AUSO","DGCU2","CRESB","LEDE","AGRO","MOLI","MOLA5","INVJ","MOLI5","BPAT","CELU","BYMAB","CARC","GBAN","CADO","TXARB","GGALD","DYCA","INTR","GGALC","COUR","METR","FERR","RICH","SAMI","TXARD","CEPUD","PATA","BMA.B","MORI","GCLAB","POLL","ALUAD","GCLA","CGPA2","GRIM","DOME","RIGO")
        val cedears = listOf("TMD","HOG","VISTD","UAL","NFLXD","AZN","MSFT","META","TSMD","AEG","UNHD","SHELD","VIV","GILDD","UBER","WMT.B","XOM","SQ","PFED","AIG","XLF","UPST","MOD","MU","GFI","GE","QQQD","GMD","NEM","RIOT","GSK","XLED","NG","MCD","GM","HUT","DOCU","MOSD","KEP","DE","PG","QCOMD","BRKB","KOD","MRVLD","XROX","DOW","ARKK","TSLAD","BBV","DESP","IWM","IBMD","TD","ABNBD","KMB","GED","AMX","MUD","YY","VALED","DAL","WBO","QCOM","BB","AMD","MSI","HSBC","VIST","DIAD","FDXD","EWZD","GLW","T","HDB","PBR","IWMD","GGB","JNJ","SAP","BIOX","NKE","F","VRSN","E","SDA","WMTD","XLE","MUX","ABBV","UNP","KO","MMMD","UNH","TCOM","SHOPD","HMC","CAT","DISN","GPRK","PKS","MA","MCDD","SBUX","SPOT","SLB","HPQD","VD","SATL","ADBED","ROKU","JPMD","TSM","BBD","USB","ORAN","BABAB","ERJ","BRKBD","BSBR","GLOBD","PLTRD","DIA","IFF","JD","EBR","PBI","TEN","EEMD","PAC","MSFTD","VZ","ZMD","CCL","BIOXD","AVGO","SNOWD","ADBE","CL","BMY","COIND","AXP","QQQDB","ARKKD","CRMD","TXR","COST","ERIC","SE","SHPW","MAD","IBM","INFY","KGC","EEM","MGLU3","AMAT","BBDD","C","SYY","TGT","AVGOD","GLOB","ASR","FMX","CRM","BIDU","NVDA","BMYD","EA","TXN","CVX","RIO","FDX","XLFD","AKO.B","CX","BHP","CAH","ORCL","WBA","LMT","BCS","SCCO","BA.C","JMIA","BP","AMGN","TRIP","QQQ","AOCA","CSCO","BA","NUE","HMYD","JNJD","RENT3","SPGID","HSY","COIN","V","MDT","ROST","JDD","YELP","SPGI","RIOD","VALE","PAAS","XOMD","NUD","BBAS3","SPYD","SPOTD","WBAD","MOS","BABAD","RTX","GS","XD","AAL","CATD","NVS","UL","PLTR","EWZ","LREN3","DED","EBAY","GOLDD","ABNB","DISND","PAGS","MSTR","URBN","PAASD","MO","ARCO","GOGLD","HAL","HSYD","HON","SED","PRIO3","RBLX","MSTRD","SBUXD","UPSTD","AALD","CDE","FSLR","LLYD","RACE","LRCX","TXND","BNG","ZM","X","GOOGL","PYPLD","PBRD","BITFD","WFCD","DESPD","NFLX","PEP","ITUB","GILD","JPM","XLEDB","IP","UGP","INTC","ACN","MDLZ","BAK","DEO","CVXD","SID","ELP","OXY","GRMN","NKED","TEND","AMDD","KOFM","METAD","VOD","ETSY","LLY","HPQ","SAN","UBERD","HMY","CAAP","MUFG","AVY","PYPL","WMT","SHOP","IBN","WFC","MELI","MELID","NIOD","EFX","BIIB","VZD","NVDAD","SBS","FCX","TM","NIO","C.D","ANF","TV","SNOW","NU","SNA","AAP","MFG","SUZ","MRVL","TTE","TMO","KMBD","TIMB","HD","AAPLD","NGG","PM","TEFO","AXPD","PEPD","DD","PANW","HWM","LND","ADGO","ABEV","TSLA","INTCD","PFE","PCAR","LAC","SPY","OXYD","CSCOD","LACD","CAR","ABBVD","BITF","NMR","MMC","ERJD","PGD","ADI","HAPV3","MRK","GOLD","ABEVD","SONY","MMM","CVS","LVS","TXRD","XP","BK","TWLO","AAPL","JMIAD","BRFS","NTES","HUTD","AMZND","NOKA","BA.CD","AMZN","SNAP","AEM","BABA","STLA","ADP","MRNA","SHEL","TRVV","HL","XPD","RACED","ABT","AZND","PSX","DJN3C","ING","PHG","EAD","DOWD","STNE","LYG")
        val letras = listOf("S30S4")

        val arr = bonos + ONs + acciones + accionesGeneral + cedears
        val mapa = hashMapOf<String,List<String>>(
            Pair("Bonos", bonos) ,
            Pair("Obligaciones negociables", ONs),
            Pair("Acciones", acciones),
            Pair("Panel General", accionesGeneral),
            Pair("Cedears", cedears),
            Pair("Letras", letras)
        )

        fun obtenerTipo(ticker:String) : String {
            mapa.keys.forEach { k ->
                if (mapa[k]!!.contains(ticker)){
                    return@obtenerTipo k
                }
            }
            return "Bonos"
            //throw Exception("No existe el activo $ticker ot")
        }

//        suspend fun obtenerListado(url:String){
//            val x = Red.conectar(url)
//            val col = x!!.select("#lideres > tbody > tr > td:nth-child(1)")
//            val txt = col.map {it.text()}
//                //.filter { !it.endsWith("Z") && !it.endsWith("X") }
//                .toSet()
//                .joinToString("\",\"","\"","\"")
//            Log.i("listado",txt)
//        }
    }
}