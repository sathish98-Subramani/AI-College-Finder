import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import { Link } from 'react-router-dom'
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'

// Default marker icons don't resolve correctly under Vite's bundler - point them at the CDN copies.
const markerIcon = new L.Icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
})

export default function MapView({ colleges, height = '420px' }) {
  const located = colleges.filter((c) => c.latitude != null && c.longitude != null)

  if (located.length === 0) {
    return (
      <div className="flex items-center justify-center rounded-2xl border border-line bg-paper-2/40 p-10 text-sm text-muted">
        No location data available for these colleges yet.
      </div>
    )
  }

  const center = [located[0].latitude, located[0].longitude]

  return (
    <div className="overflow-hidden rounded-2xl border border-line" style={{ height }}>
      <MapContainer center={center} zoom={5} scrollWheelZoom={false} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {located.map((c) => (
          <Marker key={c.id} position={[c.latitude, c.longitude]} icon={markerIcon}>
            <Popup>
              <p className="font-semibold">{c.collegeName}</p>
              <p className="text-xs text-muted">{c.city}, {c.state}</p>
              <Link to={`/college/${c.id}`} className="text-xs text-gold underline">
                View details
              </Link>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  )
}
