from fastkml import kml
from block import Block
import pickle

DATA_FILE = 'nyc.kml'
PICKLE_FILE = 'nyc.kml.pkl'

#load fastkml object from pickle, or reparse. Takes a minute.
def parseKML(p=PICKLE_FILE, f=DATA_FILE):
    try:
        with open(p, 'rb') as fi:
            return pickle.load(fi)
    except IOError:
        with open(f) as fi:
            data = fi.read().encode('utf-8')
        k = kml.KML()
        k.from_string(data)
        out = open(p, 'wb')
        pickle.dump(k, out)

        return k

#returns [(lat, lon), ...]
def getBlockBoundaries(number=10):
    fastkml = parseKML()
    blocks = list(list(fastkml.features())[0].features())[0:number]
    l_blocks = [Block(index, [(point._coordinates[1], point._coordinates[0]) for point in block._geometry.geometry._geoms[1]._exterior._geoms]) for index, block in enumerate(blocks)]

    return l_blocks
