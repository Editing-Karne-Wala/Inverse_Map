from flask import Flask, jsonify, send_from_directory, request
from flask_cors import CORS
import requests
import os

app = Flask(__name__)
CORS(app)

# User's actual key
API_KEY = "AIzaSyDOk3vCXV9qCtvXQB4oJ6GTcWMi33p3UA8"

@app.route('/')
def index():
    return send_from_directory('.', 'InverseMap_Standalone.html')

@app.route('/api/route')
def get_safe_route():
    origin = request.args.get('origin')
    destination = request.args.get('destination')

    if not origin or not destination:
        return jsonify({"error": "Missing coordinates"}), 400

    # Real Logics: Request alternatives from Google Directions with Traffic mode
    url = f"https://maps.googleapis.com/maps/api/directions/json?origin={origin}&destination={destination}&alternatives=true&departure_time=now&traffic_model=best_guess&key={API_KEY}"
    
    print(f"Requesting Route: {origin} -> {destination}")
    response = requests.get(url).json()
    
    if response['status'] != 'OK':
        print(f"GOOGLE ERROR: {response.get('status')} - {response.get('error_message', 'No details')}")
        return jsonify({"error": f"Google API Error: {response.get('status')}"}), 400

    routes = response['routes']
    
    # We loop through routes and find the 'Safest' (most crowded based on traffic ratio)
    best_route = None
    max_safety_score = -1

    for r in routes:
        leg = r['legs'][0]
        base_dur = leg['duration']['value']
        traffic_dur = leg.get('duration_in_traffic', {'value': base_dur})['value']
        
        # Crowd Factor: Higher traffic delay = Higher visibility/Crowd in our model
        crowd_factor = traffic_dur / base_dur
        
        # We also check the summary. If it's a 'Highway' or 'Expressway', we boost the score
        summary = r.get('summary', '').lower()
        if 'express' in summary or 'nh' in summary or 'bypass' in summary:
            crowd_factor *= 1.5

        if crowd_factor > max_safety_score:
            max_safety_score = crowd_factor
            best_route = r

    # Extract coordinates for the frontend to draw
    points = []
    for step in best_route['legs'][0]['steps']:
        points.append([step['end_location']['lat'], step['end_location']['lng']])

    return jsonify({
        "points": points,
        "summary": best_route['summary'],
        "safety_score": f"{int(max_safety_score * 100)}%"
    })

if __name__ == '__main__':
    app.run(port=5000)
