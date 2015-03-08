from flask import Flask
from game import Game

app = Flask(__name__)

game = Game()

@app.route("/register/<string:name>/<lat>/<lon>", methods=['POST'])
def register(name, lat, lon):
    player = game.addPlayer(name, (float(lat), float(lon)))
    return str(player.id)
    
@app.route("/update/loc/<lat>/<lon>/<int:id>", methods=['POST'])
def capture(lat, lon, id):
    player = game.playerFromId(id)
    game.updatePlayerLocation(player, (float(lat), float(lon)))
    game.processCapture(player)
    return '200'
    
@app.route("/update/status")
def status():
    return str(game.getState())

if __name__ == "__main__":
    app.run(debug=True)
